package mg.itu.aquanova.sanitaireequipement.services;

import org.springframework.stereotype.Service;
import mg.itu.aquanova.sanitaireequipement.models.TypeEquipement;
import mg.itu.aquanova.sanitaireequipement.repositories.TypeEquipementRepository;
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

    public TypeEquipement enregistrer(TypeEquipement type) {
        return repository.save(type);
    }
}