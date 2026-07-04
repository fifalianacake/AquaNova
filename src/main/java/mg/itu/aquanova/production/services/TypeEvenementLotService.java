package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.TypeEvenementLotRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TypeEvenementLotService {
    private final TypeEvenementLotRepository repository;

    public TypeEvenementLotService(TypeEvenementLotRepository repository) {
        this.repository = repository;
    }

    public List<TypeEvenementLot> listerTous() { return repository.findAll(); }
    public TypeEvenementLot trouverParId(Long id) { return repository.findById(id).orElseThrow(); }
    public TypeEvenementLot creer(TypeEvenementLot type) { return repository.save(type); }
    public TypeEvenementLot modifier(Long id, TypeEvenementLot type) {
        TypeEvenementLot existing = trouverParId(id);
        existing.setCode(type.getCode());
        existing.setLibelle(type.getLibelle());
        return repository.save(existing);
    }
    public void supprimer(Long id) { repository.deleteById(id); }
}
