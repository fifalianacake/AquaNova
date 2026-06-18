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
    @JoinColumn(name = "aliment_id", nullable = false)
    private Aliment aliment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false, length = 20)
    private TypeMouvement typeMouvement;

    @Column(name = "quantite_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteKg;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    public MouvementStock() {
    }

    public MouvementStock(LocalDate dateMouvement, Aliment aliment, TypeMouvement typeMouvement,
                           BigDecimal quantiteKg, String commentaire) {
        this.dateMouvement = dateMouvement;
        this.aliment = aliment;
        this.typeMouvement = typeMouvement;
        this.quantiteKg = quantiteKg;
        this.commentaire = commentaire;
    }

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

    public BigDecimal getQuantiteKg() {
        return quantiteKg;
    }

    public void setQuantiteKg(BigDecimal quantiteKg) {
        this.quantiteKg = quantiteKg;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}