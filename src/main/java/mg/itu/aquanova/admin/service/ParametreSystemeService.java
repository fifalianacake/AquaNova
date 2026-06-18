package mg.itu.aquanova.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.admin.models.ParametreSysteme;
import mg.itu.aquanova.admin.repositories.ParametreSystemeRepository;

@Service
public class ParametreSystemeService {
    private final ParametreSystemeRepository repository;

    public ParametreSystemeService(ParametreSystemeRepository repository) {
        this.repository = repository;
    }

    public List<ParametreSysteme> findAll() {
        return repository.findAll();
    }

    public ParametreSysteme findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ParametreSysteme findByCode(String code) {
        return repository.findByCode(code).orElse(null);
    }

    public ParametreSysteme create(ParametreSysteme parametre) {
        if (repository.existsByCode(parametre.getCode())) {
            throw new RuntimeException("Ce code de paramètre existe déjà");
        }

        return repository.save(parametre);
    }

    public ParametreSysteme update(Long id, ParametreSysteme data) {
        ParametreSysteme parametre = findById(id);

        parametre.setCode(data.getCode());
        parametre.setLibelle(data.getLibelle());
        parametre.setValeur(data.getValeur());
        parametre.setTypeValeur(data.getTypeValeur());
        parametre.setDescription(data.getDescription());

        return repository.save(parametre);
    }

    public void delete(Long id) {
        ParametreSysteme parametre = findById(id);

        if (parametre == null) {
            throw new RuntimeException("Ce code n'existe pas");
        }

        repository.delete(parametre);
    }

    public void delete(String code) {
        ParametreSysteme parametre = findByCode(code);

        if (parametre == null) {
            throw new RuntimeException("Ce paramètre n'existe pas");
        }

        repository.delete(parametre);
    }
}
