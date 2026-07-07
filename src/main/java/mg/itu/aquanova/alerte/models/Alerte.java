package mg.itu.aquanova.alerte.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerte")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_source")
    private String moduleSource; // PRODUCTION, ALIMENTATION, SANITAIRE, FINANCE

    @ManyToOne
    @JoinColumn(name = "id_type_alerte", nullable = false)
    private TypeAlerte typeAlerte;

    @ManyToOne
    @JoinColumn(name = "id_niveau_criticite", nullable = false)
    private NiveauCriticite niveauCriticite;

    @ManyToOne
    @JoinColumn(name = "id_statut_alerte", nullable = false)
    private StatutAlerte statutAlerte;

    @Column(nullable = false)
    private String message;

    @Column(name = "entite_type")
    private String entiteType; // LOT, BASSIN, ALIMENT, EQUIPEMENT...

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @Column(name = "commentaire_resolution")
    private String commentaireResolution;

    public Alerte() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModuleSource() { return moduleSource; }
    public void setModuleSource(String moduleSource) { this.moduleSource = moduleSource; }

    public TypeAlerte getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(TypeAlerte typeAlerte) { this.typeAlerte = typeAlerte; }

    public NiveauCriticite getNiveauCriticite() { return niveauCriticite; }
    public void setNiveauCriticite(NiveauCriticite niveauCriticite) { this.niveauCriticite = niveauCriticite; }

    public StatutAlerte getStatutAlerte() { return statutAlerte; }
    public void setStatutAlerte(StatutAlerte statutAlerte) { this.statutAlerte = statutAlerte; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEntiteType() { return entiteType; }
    public void setEntiteType(String entiteType) { this.entiteType = entiteType; }

    public Long getEntiteId() { return entiteId; }
    public void setEntiteId(Long entiteId) { this.entiteId = entiteId; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }

    public String getCommentaireResolution() { return commentaireResolution; }
    public void setCommentaireResolution(String c) { this.commentaireResolution = c; }
}