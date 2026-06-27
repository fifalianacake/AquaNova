package mg.itu.aquanova.stock.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.stock.models.Aliment;
import mg.itu.aquanova.stock.repositories.AlimentRepository;

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
}