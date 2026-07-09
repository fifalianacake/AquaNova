package mg.itu.aquanova.referentiel.services;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class StatutBassinService {
    private final StatutBassinRepository statutBassinRepository;

    public StatutBassinService(StatutBassinRepository repository) {
        this.statutBassinRepository = repository;
    }

    public List<StatutBassin> findAll() {
        return statutBassinRepository.findAll();
    }

    public StatutBassin findById(Long id) {
        return statutBassinRepository.findById(id).orElse(null);
    }

    public StatutBassin findByLibelle(LibelleStatutBassin libelle) {
        return statutBassinRepository.findByLibelle(libelle).orElse(null);
    }

    public StatutBassin save(StatutBassin statutBassin) {
        return statutBassinRepository.save(statutBassin);
    }

    public StatutBassin update(Long id, StatutBassin data) {
        StatutBassin statutBassin = findById(id);

        if (statutBassin == null) {
            throw new IllegalArgumentException("Statut de bassin introuvable avec l'ID : " + id);
        }

        statutBassin.setLibelle(data.getLibelle());

        return statutBassinRepository.save(statutBassin);
    }

    public void delete(Long id) {
        statutBassinRepository.deleteById(id);
    }
}
