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
        if (e.getNom() == null || e.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'espèce est obligatoire.");
        }
        if (e.getPoidsCibleMoyen() != null && e.getPoidsCibleMoyen().signum() <= 0) {
            throw new IllegalArgumentException("Le poids cible moyen doit être strictement positif.");
        }
        e.setNom(e.getNom().trim());
        return repo.save(e);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}