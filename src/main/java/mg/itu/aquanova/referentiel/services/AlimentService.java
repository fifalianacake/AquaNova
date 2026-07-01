package mg.itu.aquanova.referentiel.services;

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
        if (aliment.getTypeAliment() == null) {
            throw new IllegalArgumentException("Le type de l'aliment est obligatoire.");
        }
    }

    private void normaliserAliment(Aliment aliment) {
        aliment.setNom(aliment.getNom().trim());
        aliment.setTypeAliment(aliment.getTypeAliment());
    }

}