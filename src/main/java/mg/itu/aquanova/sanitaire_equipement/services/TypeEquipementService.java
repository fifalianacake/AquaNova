
package mg.itu.aquanova.sanitaire_equipement.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.itu.aquanova.sanitaire_equipement.models.TypeEquipement;
import mg.itu.aquanova.sanitaire_equipement.repositories.TypeEquipementRepository;

import java.util.List;

@Service
public class TypeEquipementService {

    private final TypeEquipementRepository repository;


    public TypeEquipementService(TypeEquipementRepository repository) {
        this.repository = repository;
    }

    public List<TypeEquipement> listerTous() {
        return repository.findAll();
    }

   
    public TypeEquipement trouverParId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Type d'équipement introuvable avec l'ID : " + id));
    }

    /**
     * Enregistre un nouveau type d'équipement.
     */
    @Transactional
    public TypeEquipement creer(TypeEquipement type) {
        if (type.getLibelle() == null || type.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire.");
        }
        return repository.save(type);
    }

    /**
     * Modifie un type d'équipement existant.
     */
    @Transactional
    public TypeEquipement modifier(Long id, TypeEquipement typeDetails) {
        TypeEquipement type = trouverParId(id);
        type.setLibelle(typeDetails.getLibelle());
        type.setDescription(typeDetails.getDescription());
        return repository.save(type);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Type introuvable.");
        }
        repository.deleteById(id);
    }
}