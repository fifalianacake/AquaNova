package mg.itu.aquanova.sanitaireequipement.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mg.itu.aquanova.sanitaireequipement.models.Equipement;
import mg.itu.aquanova.sanitaireequipement.repositories.EquipementRepository;
import mg.itu.aquanova.sanitaireequipement.repositories.MaintenanceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipementService {

    private final EquipementRepository repository;
    private final MaintenanceRepository maintenanceRepository;

    public EquipementService(EquipementRepository repository, MaintenanceRepository maintenanceRepository) {
        this.repository = repository;
        this.maintenanceRepository = maintenanceRepository;
    }

    // --- Fonctions CRUD ---

    public List<Equipement> listerTout() {
        return repository.findAll();
    }

    public Equipement trouverParId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Équipement introuvable avec l'ID : " + id));
    }

    @Transactional
    public Equipement creer(Equipement eq) {
        if (eq.getNom() == null || eq.getTypeEquipement() == null || eq.getStatut() == null) {
            throw new IllegalArgumentException("Le nom, le type et le statut sont obligatoires.");
        }
        return repository.save(eq);
    }

    @Transactional
    public Equipement modifier(Long id, Equipement eqDetails) {
        Equipement eq = trouverParId(id);
        eq.setNom(eqDetails.getNom());
        eq.setTypeEquipement(eqDetails.getTypeEquipement());
        eq.setBassin(eqDetails.getBassin());
        eq.setStatut(eqDetails.getStatut());
        eq.setDateInstallation(eqDetails.getDateInstallation());
        eq.setObservation(eqDetails.getObservation());
        return repository.save(eq);
    }

    @Transactional
    public void supprimer(Long id) {
        // Règle métier : empêcher la suppression si lié à une maintenance (RG03)
        if (maintenanceRepository.existsByEquipementId(id)) {
            throw new IllegalStateException("Impossible de supprimer : cet équipement est lié à un historique de maintenance.");
        }
        repository.deleteById(id);
    }

    // --- Logique métier et Recherche ---

    @Transactional
    public void updateStatut(Long id, String nouveauStatut) {
        Equipement eq = trouverParId(id);
        eq.setStatut(nouveauStatut);
        repository.save(eq);
    }

    public List<Equipement> search(String nom, Long typeId, String statut, Long bassinId) {
        return repository.findAll().stream()
            .filter(e -> (nom == null || e.getNom().toLowerCase().contains(nom.toLowerCase())))
            .filter(e -> (typeId == null || e.getTypeEquipement().getId().equals(typeId)))
            .filter(e -> (statut == null || e.getStatut().equals(statut)))
            .filter(e -> (bassinId == null || (e.getBassin() != null && e.getBassin().getId().equals(bassinId))))
            .collect(Collectors.toList());
    }

    public List<Equipement> getByBassin(Long idBassin) {
        return repository.findByBassinId(idBassin);
    }

    public List<Equipement> getByStatut(String statut) {
        return repository.findByStatut(statut);
    }
}