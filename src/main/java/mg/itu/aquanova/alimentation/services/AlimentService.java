package mg.itu.aquanova.alimentation.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.alimentation.models.Aliment;
import mg.itu.aquanova.alimentation.repositories.AlimentRepository;

@Service
public class AlimentService {

    private final AlimentRepository repo;

    public AlimentService(AlimentRepository repo) {
        this.repo = repo;
    }

    public List<Aliment> findAll() {
        return repo.findAll();
    }

    public List<Aliment> searchByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return findAll();
        }
        return repo.findByNomContainingIgnoreCaseOrderByNomAsc(nom.trim());
    }

    public Aliment findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public Aliment save(Aliment aliment) {
        validerAliment(aliment);
        normaliserAliment(aliment);
        return repo.save(aliment);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public BigDecimal getStockActuel(Integer id) {
        Aliment aliment = findById(id);
        if (aliment == null || aliment.getStockActuel() == null) {
            return BigDecimal.ZERO;
        }
        return aliment.getStockActuel();
    }

    private void validerAliment(Aliment aliment) {
        if (aliment == null) {
            throw new IllegalArgumentException("L'aliment est obligatoire.");
        }
        if (aliment.getNom() == null || aliment.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'aliment est obligatoire.");
        }
        if (aliment.getType() == null || aliment.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de l'aliment est obligatoire.");
        }
        if (aliment.getAgeMinRecommande() == null || aliment.getAgeMinRecommande() < 0) {
            throw new IllegalArgumentException("L'age minimum recommande doit etre positif ou nul.");
        }
        if (aliment.getAgeMaxRecommande() == null || aliment.getAgeMaxRecommande() < aliment.getAgeMinRecommande()) {
            throw new IllegalArgumentException("L'age maximum recommande doit etre superieur ou egal a l'age minimum.");
        }
        if (aliment.getPrixUnitaire() == null || aliment.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre positif ou nul.");
        }
        if (aliment.getTailleGranules() != null && aliment.getTailleGranules().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La taille des granules doit etre positive ou nulle.");
        }
        if (aliment.getStockActuel() != null && aliment.getStockActuel().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le stock actuel ne peut pas etre negatif.");
        }
    }

    private void normaliserAliment(Aliment aliment) {
        aliment.setNom(aliment.getNom().trim());
        aliment.setType(aliment.getType().trim());

        if (aliment.getStockActuel() == null) {
            aliment.setStockActuel(BigDecimal.ZERO);
        }
    }
}
