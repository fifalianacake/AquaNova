package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenance; 
import mg.itu.aquanova.sanitaire_equipement.repositories.CategorieMaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieMaintenanceService {

    private final CategorieMaintenanceRepository repository;

    public CategorieMaintenanceService(CategorieMaintenanceRepository repository) {
        this.repository = repository;
    }

    public CategorieMaintenance create(CategorieMaintenance categorie) {
        return repository.save(categorie);
    }

    public List<CategorieMaintenance> getAll() {
        return repository.findAll();
    }

    public Optional<CategorieMaintenance> getById(Long id) {
        return repository.findById(id);
    }

    public CategorieMaintenance update(Long id, CategorieMaintenance updatedCategorie) {
        return repository.findById(id)
                .map(existingCategorie -> {
                    
                    return repository.save(existingCategorie);
                })
                .orElseThrow(() -> new RuntimeException("Catégorie de maintenance introuvable avec l'id : " + id));
    }

    public void delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Impossible de supprimer : Catégorie de maintenance introuvable avec l'id : " + id);
        }
    }
}