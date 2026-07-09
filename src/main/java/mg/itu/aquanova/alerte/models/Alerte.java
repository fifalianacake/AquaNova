package mg.itu.aquanova.alerte.models;

import jakarta.persistence.*;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Bassin;

import java.time.LocalDateTime;

/**
 * Entité représentant une alerte système.
 * L'historique des alertes sert à l'analyse ; les alertes ne doivent pas être supprimées.
 */
@Entity
@Table(name = "alerte")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_source", nullable = false, length = 30)
    private ModuleSource moduleSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_alerte", nullable = false, length = 30)
    private TypeAlerte typeAlerte;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_criticite", nullable = false, length = 20)
    private NiveauCriticite niveauCriticite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutAlerte statut;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @ManyToOne
    @JoinColumn(name = "id_lot")
    private LotModels lot;

    @ManyToOne
    @JoinColumn(name = "id_bassin")
    private Bassin bassin;

    public Alerte() {
    }

    // ── Getters & Setters ──

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModuleSource getModuleSource() {
        return moduleSource;
    }

    public void setModuleSource(ModuleSource moduleSource) {
        this.moduleSource = moduleSource;
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public NiveauCriticite getNiveauCriticite() {
        return niveauCriticite;
    }

    public void setNiveauCriticite(NiveauCriticite niveauCriticite) {
        this.niveauCriticite = niveauCriticite;
    }

    public StatutAlerte getStatut() {
        return statut;
    }

    public void setStatut(StatutAlerte statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public Bassin getBassin() {
        return bassin;
    }

    public void setBassin(Bassin bassin) {
        this.bassin = bassin;
    }
}
