package mg.itu.aquanova.production.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.repositories.RecoltesRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class RecolteService {

    private final RecoltesRepository recoltesRepository;

    @Autowired
    public RecolteService(RecoltesRepository recoltesRepository) {
        this.recoltesRepository = recoltesRepository;
    }

    public Recoltes saveRecoltes(Recoltes recolte) {
        return recoltesRepository.save(recolte);
    }

    public List<Recoltes> getAllRecoltes() {
        return recoltesRepository.findAll();
    }

    public Recoltes getRecolteById(Long id) {
        return recoltesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Récolte introuvable avec l'id : " + id));
    }

    public Recoltes updateRecoltes(Long id, Recoltes recolteDetails) {
        Recoltes existingRecolte = getRecolteById(id);
        
        existingRecolte.setLot(recolteDetails.getLot());
        existingRecolte.setTypeRecolte(recolteDetails.getTypeRecolte());
        existingRecolte.setDateRecolte(recolteDetails.getDateRecolte());
        existingRecolte.setEffectifRecolte(recolteDetails.getEffectifRecolte());
        existingRecolte.setPoidsTotal(recolteDetails.getPoidsTotal());
        existingRecolte.setPoidsMoyen(recolteDetails.getPoidsMoyen());
        
        return recoltesRepository.save(existingRecolte);
    }

    public void deleteRecoltes(Long id) {
        Recoltes recolte = getRecolteById(id);
        recoltesRepository.delete(recolte);
    }
}