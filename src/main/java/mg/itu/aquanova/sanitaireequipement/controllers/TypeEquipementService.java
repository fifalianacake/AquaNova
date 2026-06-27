package mg.itu.aquanova.sanitaireequipement.controllers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mg.itu.aquanova.sanitaireequipement.models.TypeEquipement;
import mg.itu.aquanova.sanitaireequipement.repositories.TypeEquipementRepository;
import java.util.List;

@Service
public class TypeEquipementService {

    private final TypeEquipementRepository repository;

    // Injection par constructeur (recommandé pour Spring)
    public TypeEquipementService(TypeEquipementRepository repository) {
        this.repository = repository;
    }

    /**
     * Liste tous les types d'équipements disponibles.
     */
    public List<TypeEquipement> listerTous() {
        return repository.findAll();
    }

    /**
     * Trouve un type d'équipement par son ID.
     */
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

   
     //* Supprime un type d'équipement.
     
    @Transactional
    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Type introuvable.");
        }
        repository.deleteById(id);
    }
}