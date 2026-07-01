package mg.itu.aquanova.alimentation.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Aliment;

@Entity
@Table(name = "distribution")
public class DistributionModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_distribution", nullable = false)
    private LocalDateTime dateDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot", nullable = false)
    private LotModels lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aliment", nullable = false)
    private Aliment aliment;

    @Column(nullable = true, precision = 10, scale = 2)
    private Double quantite;

    @Column(name = "ration_theorique", precision = 10, scale = 2)
    private Double rationTheorique;

    // @Enumerated(EnumType.STRING)
    // @Column(name = "statut", nullable = false)
    // private StatutDistributionModels statut = StatutDistributionModels.EN_ATTENTE;

    public DistributionModels() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateDistribution() {
        return dateDistribution;
    }

    public void setDateDistribution(LocalDateTime dateDistribution) {
        this.dateDistribution = dateDistribution;
    }

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public Aliment getAliment() {
        return aliment;
    }

    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getRationTheorique() {
        return rationTheorique;
    }

    public void setRationTheorique(Double rationTheorique) {
        this.rationTheorique = rationTheorique;
    }
    
}
