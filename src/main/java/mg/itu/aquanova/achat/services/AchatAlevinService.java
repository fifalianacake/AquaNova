package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.dto.AchatAlevinFilter;
import mg.itu.aquanova.achat.dto.AchatAlevinForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;
import mg.itu.aquanova.achat.repositories.MouvementStockIntrantRepository;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.EspecesRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class AchatAlevinService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final LotRepository lotRepository;
    private final CategorieDepenseService categorieDepenseService;
    private final EspecesRepository especesRepository;
    private final MouvementStockIntrantRepository mouvementRepository;
    private final BassinsRepository bassinsRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final LotService lotService;
    private final StatutLotRepository statutLotRepository;

    public AchatAlevinService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            LotRepository lotRepository,
            CategorieDepenseService categorieDepenseService,
            EspecesRepository especesRepository,
            MouvementStockIntrantRepository mouvementRepository,
            BassinsRepository bassinsRepository,
            StatutBassinRepository statutBassinRepository,
            LotService lotService,
            StatutLotRepository statutLotRepository) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.lotRepository = lotRepository;
        this.categorieDepenseService = categorieDepenseService;
        this.especesRepository = especesRepository;
        this.mouvementRepository = mouvementRepository;
        this.bassinsRepository = bassinsRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.lotService = lotService;
        this.statutLotRepository = statutLotRepository;
    }

    public Achat getById(Long id) {
        return this.achatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achat introuvable avec l'ID : " + id));
    }

    public Page<Achat> listerAchatsAlevin(AchatAlevinFilter filter, Pageable pageable) {
        return achatRepository.findAll(specificationAchatsAlevins(filter), pageable);
    }

    private Specification<Achat> specificationAchatsAlevins(AchatAlevinFilter filter) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            var predicates = cb.conjunction();
            var lignes = root.join("lignes", JoinType.LEFT);
            predicates = cb.and(predicates, cb.isNotNull(lignes.get("espece").get("id")));

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
            if (filter.getEspeceId() != null) {
                predicates = cb.and(predicates, cb.equal(lignes.get("espece").get("id"), filter.getEspeceId()));
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

    @Transactional
    public Achat creerAchatAlevin(AchatAlevinForm form) {
        validerFormulaire(form);

        Fournisseur fournisseur = fournisseurRepository.findById(form.getFournisseurId())
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + form.getFournisseurId()));
        CategorieDepense categorie = categorieDepenseRepository.findById(form.getCategorieDepenseId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + form.getCategorieDepenseId()));
        EspecesModels especes = especesRepository.findById(form.getEspeceId())
                .orElseThrow(() -> new EntityNotFoundException("Espece introuvable : " + form.getEspeceId()));

        if (!categorieDepenseService.estCategorieAchatAlevin(categorie)) {
            throw new IllegalArgumentException("La catégorie doit être ACHAT_ALEVINS.");
        }

        BigDecimal montantLigne = BigDecimal.valueOf(form.getEffectif()).multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(blankToNull(form.getReferenceFacture()));
        achat.setObservation(blankToNull(null));
        achat.setStatutAchat(StatutAchat.VALIDE);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(especes.getNom());
        ligne.setEspece(especes);
        ligne.setQuantite(BigDecimal.valueOf(form.getEffectif()));
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);

        LotModels lot = new LotModels();
        lot.setEspece(especes);
        lot.setCode(String.format("LOT-%03d", lotRepository.count()));

        Optional<StatutBassin> statutBassinOptional = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE);
        if(!statutBassinOptional.isPresent()) {
            throw new IllegalArgumentException("Veuillez bien verifier le statut du bassin"); 
        }
        Optional<Bassin> bassinOptional = bassinsRepository.findByStatut(statutBassinOptional.orElse(null));
        if(!bassinOptional.isPresent()) {
            throw new IllegalArgumentException("Aucun bassin n'est libre pour effectuer cette achat d'Alevin"); 
        }
        Optional<StatutLotModels> statutLotOptional = statutLotRepository.findByLibelle(StatutLotEnum.EN_CROISSANCE);
        if(!statutLotOptional.isPresent()) {
            throw new IllegalArgumentException("Veuillez bien verifiez le statut du lot");
        }

        lot.setBassin(bassinOptional.orElse(null));
        lot.setStadeCroissance(null);
        lot.setStatutLot(statutLotOptional.orElse(null));
        lot.setDateMiseEnCharge(LocalDate.now());
        lot.setEffectifInitial(form.getEffectif());
        lot.setEffectifActuel(form.getEffectif());
        lot.setPoidsMoyenInitial(form.getPoidsMoyen().doubleValue());
        lot.setPoidsMoyenActuel(form.getPoidsMoyen().doubleValue());

        lotService.validerLot(lot, null);

        ligne.setLot(lot);
        achat.addLigne(ligne);

        Achat sauvegarde = achatRepository.save(achat);

        return sauvegarde;
    }

    private void validerFormulaire(AchatAlevinForm form) {
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
        if (form.getEspeceId() == null) {
            throw new IllegalArgumentException("L'espece est obligatoire.");
        }
        if (form.getEffectif() == null || form.getEffectif().compareTo(0) <= 0) {
            throw new IllegalArgumentException("L'effectif doit être strictement positive.");
        }
        if (form.getPoidsMoyen() == null || form.getPoidsMoyen().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le poids moyen doit exister.");
        }
        if(form.getPrixUnitaire() == null || form.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit exister.");
        }
        if(form.getMontantTotal() == null || form.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total doit exister.");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

}
