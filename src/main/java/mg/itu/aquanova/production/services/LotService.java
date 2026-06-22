package mg.itu.aquanova.production.services;

import java.util.List;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;

@Service
public class LotService {

    private final LotRepository repository;

    public LotService(LotRepository repository) {
        this.repository = repository;
    }

    public List<LotModels> listerTous() {
        return repository.findAll();
    }

    public LotModels trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + id));
    }

    public LotModels creer(LotModels lot) {
        return repository.save(lot);
    }

    public LotModels modifier(Long id, LotModels lot) {
        LotModels exist = trouverParId(id);
        exist.setCode(lot.getCode());
        exist.setEspece(lot.getEspece());
        exist.setBassin(lot.getBassin());
        exist.setStadeCroissance(lot.getStadeCroissance());
        exist.setStatutLot(lot.getStatutLot());
        exist.setDateMiseEnCharge(lot.getDateMiseEnCharge());
        exist.setEffectifInitial(lot.getEffectifInitial());
        exist.setEffectifActuel(lot.getEffectifActuel());
        exist.setPoidsMoyenInitial(lot.getPoidsMoyenInitial());
        exist.setPoidsMoyenActuel(lot.getPoidsMoyenActuel());
        exist.setObservation(lot.getObservation());
        return repository.save(exist);
    }

    public void supprimer(Long id) {
        LotModels l = trouverParId(id);
        repository.delete(l);
    }
}
