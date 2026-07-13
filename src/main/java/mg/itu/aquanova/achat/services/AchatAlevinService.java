package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.EspecesRepository;
import mg.itu.aquanova.referentiel.repositories.StadeCroissanceRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class AchatAlevinService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final CategorieDepenseService categorieDepenseService;
    private final EspecesRepository especesRepository;
    private final BassinsRepository bassinsRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final LotService lotService;
    private final StatutLotRepository statutLotRepository;
    private final StadeCroissanceRepository stadeCroissanceRepository;

    public AchatAlevinService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            CategorieDepenseService categorieDepenseService,
            EspecesRepository especesRepository,
            BassinsRepository bassinsRepository,
            StatutBassinRepository statutBassinRepository,
            LotService lotService,
            StatutLotRepository statutLotRepository,
            StadeCroissanceRepository stadeCroissanceRepository) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.categorieDepenseService = categorieDepenseService;
        this.especesRepository = especesRepository;
        this.bassinsRepository = bassinsRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.lotService = lotService;
        this.statutLotRepository = statutLotRepository;
        this.stadeCroissanceRepository = stadeCroissanceRepository;
    }

    public Achat trouverParId(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achat introuvable : " + id));
    }

    public Page<Achat> listerAchatsAlevin(AchatAlevinFilter filter, Pageable pageable) {
        return achatRepository.findAll(specificationAchatsAlevins(filter), pageable);
    }

    public List<Bassin> listerBassinsLibres() {
        StatutBassin libre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                .orElseThrow(() -> new IllegalArgumentException("Veuillez bien verifier le statut du bassin"));
        return bassinsRepository.findAllByStatutOrderByReferenceAsc(libre);
    }

    private Specification<Achat> specificationAchatsAlevins(AchatAlevinFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, existeLigneAvecEspece(root, query, cb, null));

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
                predicates = cb.and(predicates, existeLigneAvecEspece(root, query, cb, filter.getEspeceId()));
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

    private Predicate existeLigneAvecEspece(Root<Achat> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long especeId) {
        var sousRequete = query.subquery(Long.class);
        var lignes = sousRequete.from(LigneAchat.class);
        sousRequete.select(lignes.get("id"));

        var condition = cb.and(
                cb.equal(lignes.get("achat"), root),
                cb.isNotNull(lignes.get("espece").get("id")));
        if (especeId != null) {
            condition = cb.and(condition, cb.equal(lignes.get("espece").get("id"), especeId));
        }
        sousRequete.where(condition);

        return cb.exists(sousRequete);
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

        Bassin bassin = bassinsRepository.findById(form.getBassinId())
                .orElseThrow(() -> new IllegalArgumentException("Bassin introuvable : " + form.getBassinId()));
        if (bassin.getStatut() == null || bassin.getStatut().getLibelle() != LibelleStatutBassin.LIBRE) {
            throw new IllegalArgumentException("Le bassin sélectionné n'est pas libre.");
        }

        BigDecimal montantLigne = BigDecimal.valueOf(form.getEffectif()).multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(blankToNull(form.getReferenceFacture()));
        achat.setObservation(blankToNull(null));
        achat.setStatutAchat(StatutAchat.BROUILLON);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(especes.getNom());
        ligne.setEspece(especes);
        ligne.setQuantite(BigDecimal.valueOf(form.getEffectif()));
        ligne.setUnite("pièce");
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);
        ligne.setBassin(bassin);
        ligne.setPoidsMoyen(form.getPoidsMoyen());
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
        if (!categorieDepenseService.estCategorieAchatAlevin(achat.getCategorieDepense())) {
            throw new IllegalStateException("Cet achat n'est pas un achat d'alevins.");
        }

        StatutLotModels statutEnCroissance = statutLotRepository.findByLibelle(StatutLotEnum.EN_CROISSANCE)
                .orElseThrow(() -> new IllegalArgumentException("Veuillez bien vérifier le statut du lot (EN_CROISSANCE)."));

        for (LigneAchat ligne : achat.getLignes()) {
            if (ligne.getEspece() == null || ligne.getLot() != null) {
                continue;
            }
            ligne.setLot(creerLotDepuisLigne(achat, ligne, statutEnCroissance));
        }

        achat.setStatutAchat(StatutAchat.VALIDE);
        return achatRepository.save(achat);
    }

    @Transactional
    public Achat annulerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            throw new IllegalStateException("Annulation refusée : cet achat a déjà créé un lot en élevage. Clôturez ou annulez plutôt le lot concerné.");
        }
        achat.setStatutAchat(StatutAchat.ANNULE);
        return achatRepository.save(achat);
    }

    private LotModels creerLotDepuisLigne(Achat achat, LigneAchat ligne, StatutLotModels statutEnCroissance) {
        Bassin bassin = bassinsRepository.findById(ligne.getBassin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Bassin introuvable : " + ligne.getBassin().getId()));
        if (bassin.getStatut() == null || bassin.getStatut().getLibelle() != LibelleStatutBassin.LIBRE) {
            throw new IllegalStateException("Le bassin « " + bassin.getReference() + " » n'est plus libre : impossible de valider l'achat.");
        }

        BigDecimal poidsMoyen = ligne.getPoidsMoyen();
        StadeCroissanceModels stade = stadeCroissanceRepository.findAll().stream()
                .filter(s -> s.correspondAuPoids(poidsMoyen))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun stade de croissance ne correspond au poids moyen saisi (" + poidsMoyen + ")."));

        int effectif = ligne.getQuantite().intValue();
        LocalDate dateMiseEnCharge = achat.getDateAchat() != null ? achat.getDateAchat() : LocalDate.now();

        LotModels lot = new LotModels();
        lot.setEspece(ligne.getEspece());
        lot.setCode(lotService.genererCodeLot(dateMiseEnCharge));
        lot.setBassin(bassin);
        lot.setStadeCroissance(stade);
        lot.setStatutLot(statutEnCroissance);
        lot.setDateMiseEnCharge(dateMiseEnCharge);
        lot.setEffectifInitial(effectif);
        lot.setEffectifActuel(effectif);
        lot.setPoidsMoyenInitial(poidsMoyen.doubleValue());
        lot.setPoidsMoyenActuel(poidsMoyen.doubleValue());

        return lotService.creer(lot);
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
        if (form.getBassinId() == null) {
            throw new IllegalArgumentException("Le bassin est obligatoire.");
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
        // Le montant total soumis (champ calculé côté JS) n'est pas validé ici : le service
        // recalcule lui-même le montant à partir de l'effectif et du prix unitaire.
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

}
