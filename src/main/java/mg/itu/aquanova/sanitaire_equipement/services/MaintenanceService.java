package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.Maintenance;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenanceEnum;
import mg.itu.aquanova.sanitaire_equipement.models.StatutInterventionEnum;
import mg.itu.aquanova.sanitaire_equipement.repositories.MaintenanceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MaintenanceService {

    private final MaintenanceRepository repository;
    private final EquipementService equipementService;

    public MaintenanceService(MaintenanceRepository repository, EquipementService equipementService) {
        this.repository = repository;
        this.equipementService = equipementService;
    }

    private void validerMaintenance(Maintenance maintenance) {
        if (maintenance == null) {
            throw new IllegalArgumentException("La maintenance ne peut pas être nulle.");
        }
        
        if (maintenance.getEquipement() == null || maintenance.getEquipement().getId() == null) {
            throw new IllegalArgumentException("L'équipement associé est obligatoire.");
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
        List<Maintenance> maintenances = repository.findAll();
        Stream<Maintenance> stream = maintenances.stream();

        if (filter != null) {
            if (filter.getId() != null) {
                stream = stream.filter(m -> m.getId() != null && m.getId().equals(filter.getId()));
            }
            
            if (filter.getIdEquipement() != null) {
                stream = stream.filter(m -> m.getEquipement() != null && m.getEquipement().getId() != null && m.getEquipement().getId().equals(filter.getIdEquipement()));
            }
            
            if (filter.getIdUser() != null) {
                stream = stream.filter(m -> m.getUtilisateur() != null && m.getUtilisateur().getId() != null && m.getUtilisateur().getId().equals(filter.getIdUser()));
            }
            
            if (filter.getIdCategorieMaintenance() != null) {
                stream = stream.filter(m -> m.getCategorieMaintenance() != null && m.getCategorieMaintenance().getId() != null && m.getCategorieMaintenance().getId().equals(filter.getIdCategorieMaintenance()));
            }
            
            if (filter.getDebutDateMaintenance() != null) {
                LocalDate debutMaint = filter.getDebutDateMaintenance();
                stream = stream.filter(m -> m.getDateMaintenance() != null && !m.getDateMaintenance().isBefore(debutMaint));
            }
            
            if (filter.getFinDateMaintenance() != null) {
                LocalDate finMaint = filter.getFinDateMaintenance();
                stream = stream.filter(m -> m.getDateMaintenance() != null && !m.getDateMaintenance().isAfter(finMaint));
            }
            
            if (filter.getCout() != null) {
                stream = stream.filter(m -> m.getCout() != null && m.getCout().compareTo(filter.getCout()) == 0);
            }
            
            if (filter.getStatutIntervention() != null) {
                stream = stream.filter(m -> m.getStatutIntervention() == filter.getStatutIntervention());
            }
            
            if (filter.getDebutDateResolution() != null) {
                LocalDate debutRes = filter.getDebutDateResolution();
                stream = stream.filter(m -> m.getDateResolution() != null && !m.getDateResolution().isBefore(debutRes));
            }
            
            if (filter.getFinDateResolution() != null) {
                LocalDate finRes = filter.getFinDateResolution();
                stream = stream.filter(m -> m.getDateResolution() != null && !m.getDateResolution().isAfter(finRes));
            }
        }
        
        List<Maintenance> resultatFiltre = stream.toList();

        // Gestion manuelle de la pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), resultatFiltre.size());

        List<Maintenance> pageContenu = new ArrayList<>();
        if (start <= resultatFiltre.size()) {
            pageContenu = resultatFiltre.subList(start, end);
        }

        // Retour de la page Spring Data
        return new PageImpl<>(pageContenu, pageable, resultatFiltre.size());
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

        return repository.save(maintenance);
    }
}