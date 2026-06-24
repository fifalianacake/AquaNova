package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.TypeTraitementEau;
import mg.itu.aquanova.production.repositories.TypeTraitementEauRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TypeTraitementEauService {
    private final TypeTraitementEauRepository repository;

    public TypeTraitementEauService(TypeTraitementEauRepository repository) { this.repository = repository; }

    public List<TypeTraitementEau> listerTous() { return repository.findAll(); }
    public TypeTraitementEau trouverParId(Long id) { return repository.findById(id).orElseThrow(); }
    public TypeTraitementEau create(TypeTraitementEau type) { return repository.save(type); }
    public TypeTraitementEau update(Long id, TypeTraitementEau details) {
        TypeTraitementEau type = trouverParId(id);
        type.setLibelle(details.getLibelle());
        type.setDescription(details.getDescription());
        return repository.save(type);
    }
    public void delete(Long id) { repository.deleteById(id); }
}