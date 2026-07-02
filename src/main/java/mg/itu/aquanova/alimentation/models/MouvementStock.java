package mg.itu.aquanova.alimentation.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import mg.itu.aquanova.referentiel.models.Aliment;

@Entity
@Table(name = "mouvement_stock")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement")
    private LocalDate dateMouvement;

    @ManyToOne
    @JoinColumn(name = "id_aliment")
    private Aliment aliment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement")
    private TypeMouvement typeMouvement;

    private Double quantite;

    private String commentaire;

    public MouvementStock() {
    }

    // getters & setters
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