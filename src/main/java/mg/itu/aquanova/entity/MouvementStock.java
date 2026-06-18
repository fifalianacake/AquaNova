package mg.itu.aquanova.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mouvement_stock")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "aliment_id",
            nullable = false
    )
    private Aliment aliment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false)
    private TypeMouvement typeMouvement;

    @Column(name = "quantite_kg", nullable = false)
    private BigDecimal quantiteKg;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public MouvementStock() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public Aliment getAliment() {
        return aliment;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public BigDecimal getQuantiteKg() {
        return quantiteKg;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public void setQuantiteKg(BigDecimal quantiteKg) {
        this.quantiteKg = quantiteKg;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
