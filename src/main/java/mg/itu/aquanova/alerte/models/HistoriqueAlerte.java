package mg.itu.aquanova.alerte.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_alerte")
public class HistoriqueAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_alerte", nullable = false)
    private Alerte alerte;

    @Column(name = "ancien_statut", nullable = false)
    private String ancienStatut;

    @Column(name = "nouveau_statut", nullable = false)
    private String nouveauStatut;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    private String commentaire;

    // Constructeurs
    public HistoriqueAlerte() {}

    public HistoriqueAlerte(Alerte alerte, String ancienStatut, String nouveauStatut, String commentaire) {
        this.alerte = alerte;
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.commentaire = commentaire;
        this.dateChangement = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alerte getAlerte() {
        return alerte;
    }

    public void setAlerte(Alerte alerte) {
        this.alerte = alerte;
    }

    public String getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(String ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public String getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(String nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}