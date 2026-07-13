package mg.itu.aquanova.finance.dto;

import java.math.BigDecimal;

public class FinanceEvolutionDTO {

    /** Mois au format "yyyy-MM". */
    private String mois;

    private BigDecimal chiffreAffaires = BigDecimal.ZERO;
    private BigDecimal coutsDirects = BigDecimal.ZERO;
    private BigDecimal margeBrute = BigDecimal.ZERO;
    private BigDecimal depenses = BigDecimal.ZERO;
    private BigDecimal profitNet = BigDecimal.ZERO;

    public FinanceEvolutionDTO() {
    }

    public FinanceEvolutionDTO(String mois) {
        this.mois = mois;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getCoutsDirects() {
        return coutsDirects;
    }

    public void setCoutsDirects(BigDecimal coutsDirects) {
        this.coutsDirects = coutsDirects;
    }

    public BigDecimal getMargeBrute() {
        return margeBrute;
    }

    public void setMargeBrute(BigDecimal margeBrute) {
        this.margeBrute = margeBrute;
    }

    public BigDecimal getDepenses() {
        return depenses;
    }

    public void setDepenses(BigDecimal depenses) {
        this.depenses = depenses;
    }

    public BigDecimal getProfitNet() {
        return profitNet;
    }

    public void setProfitNet(BigDecimal profitNet) {
        this.profitNet = profitNet;
    }
}
