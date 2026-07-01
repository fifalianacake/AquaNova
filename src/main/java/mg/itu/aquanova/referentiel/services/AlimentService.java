package mg.itu.aquanova.referentiel.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class AlimentService {

    @Autowired
    private AlimentRepository repo;

    public List<Aliment> findAll() {
        return repo.findAll();
    }

    public Aliment findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aliment introuvable : " + id));
    }

    public Aliment create(Aliment aliment) {
        return repo.save(aliment);
    }

    public Aliment update(Aliment aliment) {

        if (!repo.existsById(aliment.getId())) {
            throw new RuntimeException("Aliment introuvable : " + aliment.getId());
        }

        return repo.save(aliment);
    }

    public void delete(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Aliment introuvable : " + id);
        }

        repo.deleteById(id);
    }

    public boolean exists(Long id) {
        return repo.existsById(id);
    }

    public List<Aliment> searchByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return findAll();
        }
        return repo.findByNomContainingIgnoreCaseOrderByNomAsc(nom.trim());
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