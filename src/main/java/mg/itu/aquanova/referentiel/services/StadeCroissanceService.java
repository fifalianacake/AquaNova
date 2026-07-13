package mg.itu.aquanova.referentiel.services;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
import mg.itu.aquanova.referentiel.repositories.StadeCroissanceRepository;

@Service
public class StadeCroissanceService {

    private final StadeCroissanceRepository repo;

    public StadeCroissanceService(StadeCroissanceRepository repo) {
        this.repo = repo;
    }

    public List<StadeCroissanceModels> findAll() {
        return repo.findAll();
    }

    public StadeCroissanceModels findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public StadeCroissanceModels save(StadeCroissanceModels stade) {
        if (stade.getNom() == null || stade.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du stade de croissance est obligatoire.");
        }
        if (stade.getPoidsMin() != null && stade.getPoidsMin().signum() < 0) {
            throw new IllegalArgumentException("Le poids minimum ne peut pas être négatif.");
        }
        if (stade.getPoidsMax() != null && stade.getPoidsMax().signum() <= 0) {
            throw new IllegalArgumentException("Le poids maximum doit être strictement positif.");
        }
        if (stade.getPoidsMin() != null && stade.getPoidsMax() != null
                && stade.getPoidsMin().compareTo(stade.getPoidsMax()) >= 0) {
            throw new IllegalArgumentException("Le poids minimum doit être strictement inférieur au poids maximum.");
        }
        stade.setNom(stade.getNom().trim());
        return repo.save(stade);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}