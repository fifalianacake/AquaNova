package mg.itu.aquanova.sanitaire_equipement.models;

import jakarta.persistence.*;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.security.models.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_equipement", nullable = false)
    private Equipement equipement;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User utilisateur; // Ou "User" selon le nom de ta classe

    @ManyToOne
    @JoinColumn(name = "id_categorie_maintenance", nullable = false)
    private CategorieMaintenance categorieMaintenance;

    @Column(name = "date_maintenance", nullable = false)
    private LocalDate dateMaintenance;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal cout;

    @Enumerated(EnumType.STRING)
    private StatutInterventionEnum statutIntervention;

    @Column(name = "date_resolution")
    private LocalDate dateResolution;

    /**
     * Dépense générée automatiquement à la clôture de l'intervention (catégorie MAINTENANCE).
     * Reste null tant que l'intervention n'est pas clôturée, ou si son coût final est nul.
     * Sert aussi de garde d'idempotence : une intervention ne génère jamais deux dépenses.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_depense")
    private Depense depense;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public Maintenance(Long id, Equipement equipement, User utilisateur,
            CategorieMaintenance categorieMaintenance, LocalDate dateMaintenance, String description, BigDecimal cout,
            StatutInterventionEnum statutIntervention, LocalDate dateResolution, String observation) {
        this.id = id;
        this.equipement = equipement;
        this.utilisateur = utilisateur;
        this.categorieMaintenance = categorieMaintenance;
        this.dateMaintenance = dateMaintenance;
        this.description = description;
        this.cout = cout;
        this.statutIntervention = statutIntervention;
        this.dateResolution = dateResolution;
        this.observation = observation;
    }
    public Maintenance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public CategorieMaintenance getCategorieMaintenance() {
        return categorieMaintenance;
    }

    public void setCategorieMaintenance(CategorieMaintenance categorieMaintenance) {
        this.categorieMaintenance = categorieMaintenance;
    }

    public LocalDate getDateMaintenance() {
        return dateMaintenance;
    }

    public void setDateMaintenance(LocalDate dateMaintenance) {
        this.dateMaintenance = dateMaintenance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public StatutInterventionEnum getStatutIntervention() {
        return statutIntervention;
    }

    public void setStatutIntervention(StatutInterventionEnum statutIntervention) {
        this.statutIntervention = statutIntervention;
    }

    public LocalDate getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDate dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Depense getDepense() {
        return depense;
    }

    public void setDepense(Depense depense) {
        this.depense = depense;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}