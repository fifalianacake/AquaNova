package mg.itu.aquanova.stock.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_lot")
public class StockLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aliment_id")
    private Aliment aliment;

    private Double quantiteInitiale;
    private Double quantiteRestante;

    private LocalDate dateEntree;

    public Long getId() {
        return id;
    }

    public Aliment getAliment() {
        return aliment;
    }

    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public Double getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(Double quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
    }

    public Double getQuantiteRestante() {
        return quantiteRestante;
    }

    public void setQuantiteRestante(Double quantiteRestante) {
        this.quantiteRestante = quantiteRestante;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }
}