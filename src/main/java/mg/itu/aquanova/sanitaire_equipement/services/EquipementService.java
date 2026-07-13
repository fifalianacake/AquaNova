package mg.itu.aquanova.sanitaire_equipement.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.itu.aquanova.sanitaire_equipement.models.Equipement;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;
import mg.itu.aquanova.sanitaire_equipement.repositories.EquipementRepository;
import mg.itu.aquanova.sanitaire_equipement.repositories.MaintenanceRepository;

@Service
public class EquipementService {

    private final EquipementRepository repository;
    private final MaintenanceRepository maintenanceRepository;

    public EquipementService(
            EquipementRepository repository,
            MaintenanceRepository maintenanceRepository) {

        this.repository = repository;
        this.maintenanceRepository = maintenanceRepository;
    }

   

    public List<Equipement> listerTout() {
        return repository.findAll();
    }

    public Equipement trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Équipement introuvable avec l'ID : " + id));
    }

    @Transactional
    public Equipement creer(Equipement equipement) {

        normaliserBassin(equipement);

        if (equipement.getNom() == null || equipement.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire.");
        }

        if (equipement.getTypeEquipement() == null) {
            throw new IllegalArgumentException("Le type d'équipement est obligatoire.");
        }

        if (equipement.getStatut() == null) {
            throw new IllegalArgumentException("Le statut est obligatoire.");
        }

        if (equipement.getDateInstallation() == null) {
            throw new IllegalArgumentException("La date d'installation est obligatoire.");
        }

        return repository.save(equipement);
    }

    private void normaliserBassin(Equipement equipement) {
        if (equipement.getBassin() != null && equipement.getBassin().getId() == null) {
            equipement.setBassin(null);
        }
    }

    @Transactional
    public Equipement modifier(Long id, Equipement equipementDetails) {

        normaliserBassin(equipementDetails);

        Equipement equipement = trouverParId(id);

        equipement.setNom(equipementDetails.getNom());
        equipement.setTypeEquipement(equipementDetails.getTypeEquipement());
        equipement.setBassin(equipementDetails.getBassin());
        equipement.setStatut(equipementDetails.getStatut());
        equipement.setDateInstallation(equipementDetails.getDateInstallation());
        equipement.setDerniereMaintenance(equipementDetails.getDerniereMaintenance());
        equipement.setObservation(equipementDetails.getObservation());

        return repository.save(equipement);
    }

    @Transactional
    public void supprimer(Long id) {

        if (maintenanceRepository.existsByEquipementId(id)) {
            throw new IllegalStateException(
                    "Impossible de supprimer : cet équipement est lié à une maintenance.");
        }

        repository.deleteById(id);
    }

  

    @Transactional
    public void updateStatut(Long id, StatutEquipement nouveauStatut) {

        Equipement equipement = trouverParId(id);

        equipement.setStatut(nouveauStatut);

        repository.save(equipement);
    }

    @Transactional
    public void updateDerniereMaintenance(Long id, LocalDate date) {

        Equipement equipement = trouverParId(id);

        equipement.setDerniereMaintenance(date);

        repository.save(equipement);
    }

    public List<Equipement> getByBassin(Long idBassin) {
        return repository.findByBassinId(idBassin);
    }

    public List<Equipement> getByStatut(StatutEquipement statut) {
        return repository.findByStatut(statut);
    }

    // ==========================
    // Recherche multicritères
    // ==========================

    public List<Equipement> search(
            String nom,
            Long typeId,
            StatutEquipement statut,
            Long bassinId) {

        return repository.findAll()
                .stream()

                .filter(e ->
                        nom == null
                                || nom.isBlank()
                                || e.getNom().toLowerCase().contains(nom.toLowerCase()))

                .filter(e ->
                        typeId == null
                                || e.getTypeEquipement().getId().equals(typeId))

                .filter(e ->
                        statut == null
                                || e.getStatut() == statut)

                .filter(e ->
                        bassinId == null
                                || (e.getBassin() != null
                                && e.getBassin().getId().equals(bassinId)))

                .toList();
    }

}