package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import mg.itu.aquanova.achat.dto.AchatProvendeFilter;
import mg.itu.aquanova.achat.dto.AchatProvendeForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.alimentation.services.MouvementService;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class AchatProvendeService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final AlimentRepository alimentRepository;
    private final MouvementService mouvementService;

    public AchatProvendeService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            AlimentRepository alimentRepository,
            MouvementService mouvementService) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.alimentRepository = alimentRepository;
        this.mouvementService = mouvementService;
    }

    public Achat trouverParId(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achat introuvable : " + id));
    }

    public Page<Achat> listerAchatsProvende(AchatProvendeFilter filter, Pageable pageable) {
        return achatRepository.findAll(specificationAchatsProvende(filter), pageable);
    }

    /**
     * Filtre "achats de provende" = achats ayant au moins une ligne avec un aliment.
     * Exprimé en EXISTS (sous-requête) plutôt qu'en JOIN + DISTINCT : un JOIN sur une
     * collection one-to-many duplique les lignes d'Achat et nécessite un SELECT
     * DISTINCT, or PostgreSQL exige que toute colonne de l'ORDER BY figure dans le
     * SELECT DISTINCT — un tri sur un champ comme fournisseur.nom échouait donc en
     * base avec "for SELECT DISTINCT, ORDER BY expressions must appear in select list".
     * EXISTS ne duplique rien : le tri fonctionne sur n'importe quelle colonne.
     */
    private Specification<Achat> specificationAchatsProvende(AchatProvendeFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, existeLigneAvecAliment(root, query, cb, null));

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
            if (filter.getAlimentId() != null) {
                predicates = cb.and(predicates, existeLigneAvecAliment(root, query, cb, filter.getAlimentId()));
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

    private Predicate existeLigneAvecAliment(Root<Achat> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long alimentId) {
        var sousRequete = query.subquery(Long.class);
        var lignes = sousRequete.from(LigneAchat.class);
        sousRequete.select(lignes.get("id"));

        var condition = cb.and(
                cb.equal(lignes.get("achat"), root),
                cb.isNotNull(lignes.get("aliment").get("id")));
        if (alimentId != null) {
            condition = cb.and(condition, cb.equal(lignes.get("aliment").get("id"), alimentId));
        }
        sousRequete.where(condition);

        return cb.exists(sousRequete);
    }

    @Transactional
    public Achat createAchatProvende(AchatProvendeForm form) {
        if (form == null) throw new IllegalArgumentException("Formulaire obligatoire.");
        if (form.getFournisseurId() == null) throw new IllegalArgumentException("Le fournisseur est obligatoire.");
        if (form.getAlimentId() == null) throw new IllegalArgumentException("L'aliment est obligatoire.");
        if (form.getQuantite() == null || form.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive.");
        }
        if (form.getPrixUnitaire() == null || form.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas être négatif.");
        }
        if (form.getCategorieDepenseId() == null) {
            throw new IllegalArgumentException("Catégorie ACHAT_PROVENDE obligatoire.");
        }

        Fournisseur fournisseur = fournisseurRepository.findById(form.getFournisseurId())
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable"));
        CategorieDepense categorie = categorieDepenseRepository.findById(form.getCategorieDepenseId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
        Aliment aliment = alimentRepository.findById(form.getAlimentId())
                .orElseThrow(() -> new EntityNotFoundException("Aliment introuvable"));

        if (!"ACHAT_PROVENDE".equalsIgnoreCase(categorie.getCode())) {
            throw new IllegalArgumentException("La catégorie doit être ACHAT_PROVENDE.");
        }

        BigDecimal montantLigne = form.getQuantite().multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat() != null ? form.getDateAchat() : LocalDate.now());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(form.getReferenceFacture());
        achat.setObservation(form.getObservation());
        achat.setStatutAchat(StatutAchat.BROUILLON);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(aliment.getNom());
        ligne.setAliment(aliment);
        ligne.setQuantite(form.getQuantite());
        ligne.setUnite("kg"); // Unité par défaut pour la provende
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);
        ligne.setObservation(form.getObservation());
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
        if (achat.getCategorieDepense() == null || !"ACHAT_PROVENDE".equalsIgnoreCase(achat.getCategorieDepense().getCode())) {
            throw new IllegalStateException("Cet achat n'est pas un achat de provende.");
        }

        for (LigneAchat ligne : achat.getLignes()) {
            if (ligne.getAliment() != null) {
                createMouvementStockEntree(achat, ligne);
            }
        }

        achat.setStatutAchat(StatutAchat.VALIDE);
        return achatRepository.save(achat);
    }

    public void createMouvementStockEntree(Achat achat, LigneAchat ligne) {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setDateMouvement(achat.getDateAchat() != null ? achat.getDateAchat() : LocalDate.now());
        mouvement.setAliment(ligne.getAliment());
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setQuantite(ligne.getQuantite().doubleValue());
        mouvement.setCommentaire("Entrée automatique suite à la validation de l'achat provende #" + achat.getId());
        mouvementService.create(mouvement);
    }

    @Transactional
    public Achat annulerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            throw new IllegalStateException("Annulation refusée : cet achat a déjà alimenté le stock d'aliments. Créez plutôt un mouvement de correction si nécessaire.");
        }
        achat.setStatutAchat(StatutAchat.ANNULE);
        return achatRepository.save(achat);
    }
}
