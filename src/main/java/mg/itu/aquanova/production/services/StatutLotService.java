package mg.itu.aquanova.production.services;

import org.springframework.stereotype.Service;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class StatutLotService {

    private final StatutLotRepository repository;

    public StatutLotService(StatutLotRepository repository) {
        this.repository = repository;
    }

    public List<StatutLotModels> listerTous() {
        return repository.findAll();
    }

    public StatutLotModels trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Statut introuvable: " + id));
    }

    public StatutLotModels creer(StatutLotModels s) {
        return repository.save(s);
    }

    public StatutLotModels modifier(Long id, StatutLotModels s) {
        StatutLotModels exist = trouverParId(id);
        exist.setLibelle(s.getLibelle());
        exist.setDescription(s.getDescription());
        return repository.save(exist);
    }

    public void supprimer(Long id) {
        StatutLotModels s = trouverParId(id);
        repository.delete(s);
    }
}
