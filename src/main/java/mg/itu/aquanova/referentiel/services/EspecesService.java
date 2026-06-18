package mg.itu.aquanova.referentiel.services;

import java.util.List;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.repositories.EspecesRepository;

@Service
public class EspecesService {

    private final EspecesRepository repo;

    public EspecesService(EspecesRepository repo) {
        this.repo = repo;
    }

    public List<EspecesModels> findAll() {
        return repo.findAll();
    }

    public EspecesModels findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public EspecesModels save(EspecesModels e) {
        return repo.save(e);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}