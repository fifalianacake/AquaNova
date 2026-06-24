package mg.itu.aquanova.production.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @ManyToOne
    @JoinColumn(name = "id_aliment", nullable = false)
    private Aliment aliment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false)
    private TypeMouvement typeMouvement;

    @Column(nullable = false)
    private Double quantite;

    private String commentaire;

    // Getters et Setters standard
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public Aliment getAliment() {
        return aliment;
    }

    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}