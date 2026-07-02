package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.Maintenance;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenanceEnum;
import mg.itu.aquanova.sanitaire_equipement.models.Equipement;
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
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MaintenanceService {

    private final MaintenanceRepository repository;

    public MaintenanceService(MaintenanceRepository repository) {
        this.repository = repository;
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

        if(maintenance.getCategorieMaintenance().getLibelle() == CategorieMaintenanceEnum.PANNE) {
            updateStatutEquipement(maintenance, StatutEquipement.EN_PANNE);
        } else {
            updateStatutEquipement(maintenance, StatutEquipement.EN_MAINTENANCE);
        }

        Maintenance savedMaintenance = repository.save(maintenance);
        
        return savedMaintenance;
    }

    @Transactional
    public Maintenance update(Long id, Maintenance updatedMaintenance) {
        validerMaintenance(updatedMaintenance);

        return repository.findById(id)
                .map(existing -> {
                    existing.setEquipement(updatedMaintenance.getEquipement());
                    existing.setUtilisateur(updatedMaintenance.getUtilisateur());
                    existing.setCategorieMaintenance(updatedMaintenance.getCategorieMaintenance());
                    existing.setDateMaintenance(updatedMaintenance.getDateMaintenance());
                    existing.setDescription(updatedMaintenance.getDescription());
                    existing.setCout(updatedMaintenance.getCout());
                    existing.setStatutIntervention(updatedMaintenance.getStatutIntervention());
                    existing.setDateResolution(updatedMaintenance.getDateResolution());
                    existing.setObservation(updatedMaintenance.getObservation());
                    
                    Maintenance saved = repository.save(existing);
                    updateStatutEquipement(saved, StatutEquipement.DISPONIBLE);
                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("Maintenance introuvable avec l'id : " + id));
    }

    @Transactional
    public void delete(Long id) {
        Maintenance maintenance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance introuvable avec l'id : " + id));
        
        repository.deleteById(id);
        updateStatutEquipement(maintenance.getEquipement(), StatutEquipement.DISPONIBLE);
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

        return repository.findById(id)
                .map(maintenance -> {
                    maintenance.setStatutIntervention(StatutInterventionEnum.TERMINEE);
                    maintenance.setDateResolution(LocalDate.now());
                    if (observation != null) maintenance.setObservation(observation);
                    if (coutFinal != null) maintenance.setCout(coutFinal);
                    
                    Maintenance saved = repository.save(maintenance);
                    
                    updateDerniereMaintenanceEquipement(saved.getEquipement(), saved.getDateResolution());
                    updateStatutEquipement(saved, StatutEquipement.DISPONIBLE);
                    
                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("Impossible de clôturer : Intervention introuvable (ID: " + id + ")"));
    }

    public void updateDerniereMaintenanceEquipement(Equipement equipement, LocalDate dateResolution) {
        if (equipement != null) {
            System.out.println("Mise à jour de la date de dernière maintenance pour l'équipement " + equipement.getId());
        }
    }

    public void updateStatutEquipement(Maintenance maintenance, StatutEquipement statut) {
        if (maintenance.getEquipement() != null) {
            maintenance.getEquipement().setStatut(statut);
        }
    }
}