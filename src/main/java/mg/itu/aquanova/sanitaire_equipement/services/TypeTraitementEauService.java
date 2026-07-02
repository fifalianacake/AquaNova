package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.models.TypeTraitementEau;
import mg.itu.aquanova.sanitaire_equipement.repositories.TypeTraitementEauRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeTraitementEauService {
    private final TypeTraitementEauRepository repository;

    public TypeTraitementEauService(TypeTraitementEauRepository repository) {
        this.repository = repository;
    }

    public List<TypeTraitementEau> listerTous() {
        return repository.findAll();
    }

    public TypeTraitementEau trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Type de traitement introuvable"));
    }

    public TypeTraitementEau create(TypeTraitementEau type) {
        validate(type);
        return repository.save(type);
    }

    public TypeTraitementEau update(Long id, TypeTraitementEau typeDetails) {
        TypeTraitementEau type = trouverParId(id);
        validate(typeDetails);

        type.setLibelle(typeDetails.getLibelle());
        type.setDescription(typeDetails.getDescription());

        return repository.save(type);
    }

    public void delete(Long id) {
        trouverParId(id);
        repository.deleteById(id);
    }

    private void validate(TypeTraitementEau type) {
        if (type == null || type.getLibelle() == null || type.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé du type de traitement est obligatoire");
        }
    }
}
