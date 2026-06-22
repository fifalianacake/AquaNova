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
        return repo.save(stade);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}