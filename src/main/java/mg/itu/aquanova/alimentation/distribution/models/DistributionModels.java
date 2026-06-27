package mg.itu.aquanova.alimentation.distribution.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.TypeAlimentModels;

@Entity
@Table(name = "distribution")
public class DistributionModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_distribution", nullable = false)
    private LocalDate dateDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot", nullable = false)
    private LotModels lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aliment", nullable = false)
    private TypeAlimentModels aliment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;

    @Column(name = "ration_theorique", precision = 10, scale = 2)
    private BigDecimal rationTheorique;

    public DistributionModels() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDistribution() {
        return dateDistribution;
    }

    public void setDateDistribution(LocalDate dateDistribution) {
        this.dateDistribution = dateDistribution;
    }

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public TypeAlimentModels getAliment() {
        return aliment;
    }

    public void setAliment(TypeAlimentModels aliment) {
        this.aliment = aliment;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getRationTheorique() {
        return rationTheorique;
    }

    public void setRationTheorique(BigDecimal rationTheorique) {
        this.rationTheorique = rationTheorique;
    }
}