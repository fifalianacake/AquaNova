package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mouvement_stock_intrant")
public class MouvementStockIntrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @ManyToOne
    @JoinColumn(name = "id_intrant", nullable = false)
    private Intrant intrant;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false, length = 30)
    private TypeMouvementIntrant typeMouvement;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ligne_achat")
    private LigneAchat ligneAchat;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    public MouvementStockIntrant() {
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

    public Intrant getIntrant() {
        return intrant;
    }

    public void setIntrant(Intrant intrant) {
        this.intrant = intrant;
    }

    public TypeMouvementIntrant getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvementIntrant typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public LigneAchat getLigneAchat() {
        return ligneAchat;
    }

    public void setLigneAchat(LigneAchat ligneAchat) {
        this.ligneAchat = ligneAchat;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
