package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.TypeTraitementEau;
import mg.itu.aquanova.sanitaire_equipement.repositories.TypeTraitementEauRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TypeTraitementEauService {
    private final TypeTraitementEauRepository repository;

    public TypeTraitementEauService(TypeTraitementEauRepository repository) { this.repository = repository; }

    public List<TypeTraitementEau> listerTous() { return repository.findAll(); }
    public TypeTraitementEau trouverParId(Long id) { return repository.findById(id).orElseThrow(); }
    public TypeTraitementEau create(TypeTraitementEau type) { return repository.save(type); }
}