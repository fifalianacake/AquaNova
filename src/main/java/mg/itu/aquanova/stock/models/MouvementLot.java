package mg.itu.aquanova.stock.models;

import jakarta.persistence.*;

@Entity
@Table(name = "mouvement_lot")
public class MouvementLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mouvement_id")
    private MouvementStock mouvement;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private StockLot lot;

    private Double quantite;

    public Long getId() {
        return id;
    }

    public MouvementStock getMouvement() {
        return mouvement;
    }

    public void setMouvement(MouvementStock mouvement) {
        this.mouvement = mouvement;
    }

    public StockLot getLot() {
        return lot;
    }

    public void setLot(StockLot lot) {
        this.lot = lot;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
}