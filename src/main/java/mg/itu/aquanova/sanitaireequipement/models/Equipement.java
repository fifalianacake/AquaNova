package mg.itu.aquanova.sanitaireequipement.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import mg.itu.aquanova.referentiel.models.Bassin;

@Entity
@Table(name = "equipement")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "id_type_equipement", nullable = false)
    private TypeEquipement typeEquipement;

    @ManyToOne
    @JoinColumn(name = "id_bassin") 
    private Bassin bassin;

    private String statut; // DISPONIBLE, EN_SERVICE, EN_PANNE, EN_MAINTENANCE, HORS_SERVICE

    @Column(name = "date_installation")
    private LocalDate dateInstallation;

    @Column(name = "derniere_maintenance")
    private LocalDate derniereMaintenance;

    private String observation;

    public Equipement() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public TypeEquipement getTypeEquipement() { return typeEquipement; }
    public void setTypeEquipement(TypeEquipement typeEquipement) { this.typeEquipement = typeEquipement; }
    public Bassin getBassin() { return bassin; }
    public void setBassin(Bassin bassin) { this.bassin = bassin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateInstallation() { return dateInstallation; }
    public void setDateInstallation(LocalDate dateInstallation) { this.dateInstallation = dateInstallation; }
    public LocalDate getDerniereMaintenance() { return derniereMaintenance; }
    public void setDerniereMaintenance(LocalDate derniereMaintenance) { this.derniereMaintenance = derniereMaintenance; }
    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}