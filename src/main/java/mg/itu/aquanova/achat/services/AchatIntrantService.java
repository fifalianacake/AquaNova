package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import mg.itu.aquanova.achat.dto.AchatIntrantFilter;
import mg.itu.aquanova.achat.dto.AchatIntrantForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.Intrant;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.MouvementStockIntrant;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.models.TypeMouvementIntrant;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;
import mg.itu.aquanova.achat.repositories.IntrantRepository;
import mg.itu.aquanova.achat.repositories.MouvementStockIntrantRepository;

@Service
public class AchatIntrantService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final CategorieDepenseService categorieDepenseService;
    private final IntrantRepository intrantRepository;
    private final MouvementStockIntrantRepository mouvementRepository;

    public AchatIntrantService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            CategorieDepenseService categorieDepenseService,
            IntrantRepository intrantRepository,
            MouvementStockIntrantRepository mouvementRepository) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.categorieDepenseService = categorieDepenseService;
        this.intrantRepository = intrantRepository;
        this.mouvementRepository = mouvementRepository;
    }

    public Page<Achat> listerAchatsIntrants(AchatIntrantFilter filter, Pageable pageable) {
        return achatRepository.findAll(specificationAchatsIntrants(filter), pageable);
    }

    public Achat trouverParId(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achat introuvable : " + id));
    }

    @Transactional
    public Achat creerAchatIntrant(AchatIntrantForm form) {
        validerFormulaire(form);

        Fournisseur fournisseur = fournisseurRepository.findById(form.getFournisseurId())
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + form.getFournisseurId()));
        CategorieDepense categorie = categorieDepenseRepository.findById(form.getCategorieDepenseId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + form.getCategorieDepenseId()));
        Intrant intrant = intrantRepository.findById(form.getIntrantId())
                .orElseThrow(() -> new EntityNotFoundException("Intrant introuvable : " + form.getIntrantId()));

        if (!categorieDepenseService.estCategorieAchatIntrant(categorie)) {
            throw new IllegalArgumentException("La catégorie doit être ACHAT_MEDICAMENT ou PRODUIT_TRAITEMENT.");
        }

        BigDecimal montantLigne = form.getQuantite().multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(blankToNull(form.getReferenceFacture()));
        achat.setObservation(blankToNull(form.getObservation()));
        achat.setStatutAchat(StatutAchat.BROUILLON);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(intrant.getNom());
        ligne.setIntrant(intrant);
        ligne.setQuantite(form.getQuantite());
        ligne.setUnite(resolveUnite(form, intrant));
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);
        ligne.setObservation(blankToNull(form.getObservation()));
        achat.addLigne(ligne);

        Achat sauvegarde = achatRepository.save(achat);

        if (form.isValiderDirectement()) {
            return validerAchat(sauvegarde.getId());
        }

        return sauvegarde;
    }

    @Transactional
    public Achat validerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            return achat;
        }
        if (achat.getStatutAchat() == StatutAchat.ANNULE) {
            throw new IllegalStateException("Un achat annulé ne peut pas être validé.");
        }
        if (!categorieDepenseService.estCategorieAchatIntrant(achat.getCategorieDepense())) {
            throw new IllegalStateException("Cet achat n'est pas un achat d'intrants.");
        }

        for (LigneAchat ligne : achat.getLignes()) {
            if (ligne.getIntrant() == null) {
                continue;
            }
            boolean mouvementExiste = ligne.getId() != null
                    && mouvementRepository.existsByLigneAchatIdAndTypeMouvement(ligne.getId(), TypeMouvementIntrant.ENTREE);
            if (!mouvementExiste) {
                MouvementStockIntrant mouvement = new MouvementStockIntrant();
                mouvement.setDateMouvement(achat.getDateAchat() != null ? achat.getDateAchat() : LocalDate.now());
                mouvement.setIntrant(ligne.getIntrant());
                mouvement.setTypeMouvement(TypeMouvementIntrant.ENTREE);
                mouvement.setQuantite(ligne.getQuantite());
                mouvement.setLigneAchat(ligne);
                mouvement.setCommentaire("Entrée automatique suite à la validation de l'achat #" + achat.getId());
                mouvementRepository.save(mouvement);
            }
        }

        achat.setStatutAchat(StatutAchat.VALIDE);
        return achatRepository.save(achat);
    }

    @Transactional
    public Achat annulerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            throw new IllegalStateException("Annulation refusée : cet achat a déjà alimenté le stock. Créez plutôt un mouvement de correction si nécessaire.");
        }
        achat.setStatutAchat(StatutAchat.ANNULE);
        return achatRepository.save(achat);
    }

    private Specification<Achat> specificationAchatsIntrants(AchatIntrantFilter filter) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            var predicates = cb.conjunction();
            var lignes = root.join("lignes", JoinType.LEFT);
            predicates = cb.and(predicates, cb.isNotNull(lignes.get("intrant").get("id")));

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getFournisseurId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("fournisseur").get("id"), filter.getFournisseurId()));
            }
            if (filter.getCategorieDepenseId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categorieDepense").get("id"), filter.getCategorieDepenseId()));
            }
            if (filter.getIntrantId() != null) {
                predicates = cb.and(predicates, cb.equal(lignes.get("intrant").get("id"), filter.getIntrantId()));
            }
            if (filter.getStatutAchat() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("statutAchat"), filter.getStatutAchat()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateAchat"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateAchat"), filter.getDateFin()));
            }
            if (filter.getMontantMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("montantTotal"), filter.getMontantMin()));
            }
            if (filter.getMontantMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("montantTotal"), filter.getMontantMax()));
            }
            if (filter.getReferenceFacture() != null && !filter.getReferenceFacture().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("referenceFacture")), "%" + filter.getReferenceFacture().trim().toLowerCase() + "%"));
            }

            return predicates;
        };
    }

    private void validerFormulaire(AchatIntrantForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Le formulaire d'achat est obligatoire.");
        }
        if (form.getDateAchat() == null) {
            throw new IllegalArgumentException("La date d'achat est obligatoire.");
        }
        if (form.getFournisseurId() == null) {
            throw new IllegalArgumentException("Le fournisseur est obligatoire.");
        }
        if (form.getCategorieDepenseId() == null) {
            throw new IllegalArgumentException("La catégorie de dépense est obligatoire.");
        }
        if (form.getIntrantId() == null) {
            throw new IllegalArgumentException("L'intrant est obligatoire.");
        }
        if (form.getQuantite() == null || form.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive.");
        }
        if (form.getPrixUnitaire() == null || form.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas être négatif.");
        }
    }

    private String resolveUnite(AchatIntrantForm form, Intrant intrant) {
        if (form.getUnite() != null && !form.getUnite().isBlank()) {
            return form.getUnite().trim();
        }
        return intrant.getUnite();
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
