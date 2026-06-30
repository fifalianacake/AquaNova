package mg.itu.aquanova.alimentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DistributionDTO {

    private Long id;
    private LocalDateTime dateDistribution;
    private Long idLot;
    private Long idAliment;
    private BigDecimal quantite;
    private BigDecimal rationTheorique;

    public DistributionDTO() {
    }

    public DistributionDTO(Long id, LocalDateTime dateDistribution, Long idLot, Long idAliment, BigDecimal quantite,
            BigDecimal rationTheorique) {
        this.id = id;
        this.dateDistribution = dateDistribution;
        this.idLot = idLot;
        this.idAliment = idAliment;
        this.quantite = quantite;
        this.rationTheorique = rationTheorique;
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

    public Long getIdLot() {
        return idLot;
    }

    public void setIdLot(Long idLot) {
        this.idLot = idLot;
    }

    public Long getIdAliment() {
        return idAliment;
    }

    public void setIdAliment(Long idAliment) {
        this.idAliment = idAliment;
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
