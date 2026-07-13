package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.DepenseService;
import mg.itu.aquanova.sanitaire_equipement.models.Maintenance;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenanceEnum;
import mg.itu.aquanova.sanitaire_equipement.models.StatutInterventionEnum;
import mg.itu.aquanova.sanitaire_equipement.repositories.MaintenanceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    /** Code de la catégorie de dépense sous laquelle sont comptabilisées les interventions. */
    private static final String CODE_CATEGORIE_MAINTENANCE = "MAINTENANCE";

    private final MaintenanceRepository repository;
    private final EquipementService equipementService;
    private final DepenseService depenseService;
    private final CategorieDepenseService categorieDepenseService;

    public MaintenanceService(
            MaintenanceRepository repository,
            EquipementService equipementService,
            DepenseService depenseService,
            CategorieDepenseService categorieDepenseService) {
        this.repository = repository;
        this.equipementService = equipementService;
        this.depenseService = depenseService;
        this.categorieDepenseService = categorieDepenseService;
    }

    private void validerMaintenance(Maintenance maintenance) {
        if (maintenance == null) {
            throw new IllegalArgumentException("La maintenance ne peut pas être nulle.");
        }
        
        if (maintenance.getEquipement() == null || maintenance.getEquipement().getId() == null) {
            throw new IllegalArgumentException("L'équipement associé est obligatoire.");
        }

        if (maintenance.getUtilisateur() == null || maintenance.getUtilisateur().getId() == null) {
            throw new IllegalArgumentException("L'auteur de l'intervention est obligatoire : reconnectez-vous.");
        }

        if (maintenance.getCategorieMaintenance() == null || maintenance.getCategorieMaintenance().getId() == null) {
            throw new IllegalArgumentException("La catégorie de maintenance est obligatoire.");
        }
        
        if (maintenance.getDateMaintenance() == null) {
            throw new IllegalArgumentException("La date de maintenance est obligatoire.");
        }
        
        if (maintenance.getDescription() == null || maintenance.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description de la maintenance est obligatoire.");
        }
        
        if (maintenance.getCout() != null && maintenance.getCout().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le coût de l'intervention ne peut pas être négatif.");
        }
        
        if (maintenance.getStatutIntervention() == null) {
            throw new IllegalArgumentException("Le statut de l'intervention est obligatoire.");
        }
    }

    public Maintenance findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance introuvable avec l'id : " + id));
    }

    public Page<Maintenance> lister(MaintenanceFilter filter, Pageable pageable) {
        return repository.findAll(specification(filter), pageable);
    }

    private Specification<Maintenance> specification(MaintenanceFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateMaintenance"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateMaintenance"), filter.getDateFin()));
            }
            if (filter.getCoutMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("cout"), filter.getCoutMin()));
            }
            if (filter.getCoutMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("cout"), filter.getCoutMax()));
            }

            return predicates;
        };
    }

    @Transactional
    public Maintenance create(Maintenance maintenance) {
        if (maintenance.getStatutIntervention() == null) {
            maintenance.setStatutIntervention(StatutInterventionEnum.OUVERTE);
        }

        validerMaintenance(maintenance);

        if (maintenance.getStatutIntervention() == StatutInterventionEnum.TERMINEE) {
            throw new IllegalArgumentException(
                    "Une intervention ne peut pas être créée déjà clôturée : déclarez-la puis utilisez "
                            + "l'action de clôture, qui seule met correctement à jour l'équipement.");
        }

        StatutEquipement statutPendantIntervention = maintenance.getCategorieMaintenance().getLibelle() == CategorieMaintenanceEnum.PANNE
                ? StatutEquipement.EN_PANNE
                : StatutEquipement.EN_MAINTENANCE;
        equipementService.updateStatut(maintenance.getEquipement().getId(), statutPendantIntervention);

        Maintenance savedMaintenance = repository.save(maintenance);

        return savedMaintenance;
    }

    @Transactional
    public Maintenance update(Long id, Maintenance updatedMaintenance) {
        validerMaintenance(updatedMaintenance);

        Maintenance existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance introuvable avec l'id : " + id));

        if (existing.getStatutIntervention() == StatutInterventionEnum.TERMINEE) {
            throw new IllegalStateException(
                    "Cette intervention est clôturée : elle ne peut plus être modifiée.");
        }

        if (updatedMaintenance.getStatutIntervention() == StatutInterventionEnum.TERMINEE) {
            throw new IllegalStateException(
                    "Utilisez l'action de clôture pour terminer une intervention : elle seule remet "
                            + "correctement à jour le statut et la date de dernière maintenance de l'équipement.");
        }

        existing.setEquipement(updatedMaintenance.getEquipement());
        existing.setUtilisateur(updatedMaintenance.getUtilisateur());
        existing.setCategorieMaintenance(updatedMaintenance.getCategorieMaintenance());
        existing.setDateMaintenance(updatedMaintenance.getDateMaintenance());
        existing.setDescription(updatedMaintenance.getDescription());
        existing.setCout(updatedMaintenance.getCout());
        existing.setStatutIntervention(updatedMaintenance.getStatutIntervention());
        existing.setObservation(updatedMaintenance.getObservation());

        return repository.save(existing);
    }

    public List<Maintenance> getByEquipement(Long idEquipement) {
        return repository.findByEquipementId(idEquipement);
    }

    public List<Maintenance> getMaintenancesOuvertes() {
        return repository.findByStatutIntervention(StatutInterventionEnum.OUVERTE);
    }

    @Transactional
    public Maintenance cloturerIntervention(Long id, String observation, BigDecimal coutFinal) {
        if (coutFinal != null && coutFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le coût final ne peut pas être négatif.");
        }

        Maintenance maintenance = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Impossible de clôturer : Intervention introuvable (ID: " + id + ")"));

        if (maintenance.getStatutIntervention() == StatutInterventionEnum.TERMINEE) {
            throw new IllegalStateException("Cette intervention est déjà clôturée.");
        }

        LocalDate dateResolution = LocalDate.now();

        maintenance.setStatutIntervention(StatutInterventionEnum.TERMINEE);
        maintenance.setDateResolution(dateResolution);
        if (observation != null) maintenance.setObservation(observation);
        if (coutFinal != null) maintenance.setCout(coutFinal);

        Long equipementId = maintenance.getEquipement().getId();
        // Un équipement affecté à un bassin retourne en service ; un équipement
        // général (non affecté) redevient simplement disponible.
        StatutEquipement statutApresCloture = maintenance.getEquipement().getBassin() != null
                ? StatutEquipement.EN_SERVICE
                : StatutEquipement.DISPONIBLE;
        equipementService.updateStatut(equipementId, statutApresCloture);
        equipementService.updateDerniereMaintenance(equipementId, dateResolution);

        comptabiliserDepense(maintenance, dateResolution);

        return repository.save(maintenance);
    }

    /**
     * Comptabilise le coût final de l'intervention sous forme de dépense (catégorie MAINTENANCE).
     *
     * La dépense n'est générée qu'à la clôture, seul moment où le coût est définitif : une
     * intervention clôturée n'est plus modifiable, la dépense ne peut donc jamais diverger.
     * Une intervention sans frais (coût nul ou non renseigné : nettoyage interne, garantie…)
     * ne génère aucune dépense — cohérent avec la règle « montant strictement positif ».
     * Le lien porté par {@code maintenance.depense} garantit l'idempotence.
     */
    private void comptabiliserDepense(Maintenance maintenance, LocalDate dateResolution) {
        if (maintenance.getDepense() != null) {
            return;
        }
        BigDecimal cout = maintenance.getCout();
        if (cout == null || cout.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Depense depense = new Depense();
        depense.setDateDepense(dateResolution);
        depense.setCategorieDepense(categorieDepenseService.trouverParCode(CODE_CATEGORIE_MAINTENANCE));
        depense.setMontant(cout);
        depense.setLibelle(libelleDepense(maintenance));
        depense.setObservation(maintenance.getObservation());

        maintenance.setDepense(depenseService.enregistrer(depense));
    }

    private String libelleDepense(Maintenance maintenance) {
        String equipement = maintenance.getEquipement() != null && maintenance.getEquipement().getNom() != null
                ? maintenance.getEquipement().getNom()
                : "équipement inconnu";
        return "Maintenance #" + maintenance.getId() + " - " + equipement;
    }
}