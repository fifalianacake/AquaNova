package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.Maintenance; 
import mg.itu.aquanova.sanitaire_equipement.repositories.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    private final MaintenanceRepository repository;

    public MaintenanceService(MaintenanceRepository repository) {
        this.repository = repository;
    }

    public Maintenance create(Maintenance maintenance) {
        return repository.save(maintenance);
    }

    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    public Optional<Maintenance> getById(Long id) {
        return repository.findById(id);
    }

    public Maintenance update(Long id, Maintenance updatedMaintenance) {
        return repository.findById(id)
                .map(existingMaintenance -> {
                    
                    return repository.save(existingMaintenance);
                })
                .orElseThrow(() -> new RuntimeException("Maintenance introuvable avec l'id : " + id));
    }

    public void delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Impossible de supprimer : Maintenance introuvable avec l'id : " + id);
        }
    }
}