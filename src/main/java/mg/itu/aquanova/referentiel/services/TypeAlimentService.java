package mg.itu.aquanova.referentiel.services;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.referentiel.repositories.TypeAlimentRepository;

@Service
public class TypeAlimentService {

    private final TypeAlimentRepository repo;

    public TypeAlimentService(TypeAlimentRepository repo) {
        this.repo = repo;
    }

    public List<TypeAlimentModels> findAll() {
        return repo.findAll();
    }

    public TypeAlimentModels findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public TypeAlimentModels save(TypeAlimentModels type) {
        if (type.getNom() == null || type.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du type d'aliment est obligatoire.");
        }
        type.setNom(type.getNom().trim());
        return repo.save(type);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}