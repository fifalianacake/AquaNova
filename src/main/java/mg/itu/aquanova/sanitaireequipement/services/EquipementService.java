package mg.itu.aquanova.sanitaireequipement.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mg.itu.aquanova.sanitaireequipement.models.Equipement;
import mg.itu.aquanova.sanitaireequipement.repositories.EquipementRepository;
import java.util.List;

@Service
public class EquipementService {

    private final EquipementRepository repository;

    public EquipementService(EquipementRepository repository) {
        this.repository = repository;
    }

    public List<Equipement> listerTout() { return repository.findAll(); }

    @Transactional
    public Equipement creer(Equipement eq) {
        // Validation : nom, type et statut obligatoires
        return repository.save(eq);
    }

    @Transactional
    public void mettreAJourStatut(Long id, String nouveauStatut) {
        Equipement eq = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Équipement introuvable"));
        eq.setStatut(nouveauStatut);
        repository.save(eq);
    }

    // Autres méthodes : update, delete, search...
}