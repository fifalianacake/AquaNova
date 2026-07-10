package mg.itu.aquanova.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CoutRevientLotDTO {

    private Long lotId;
    private String lotCode;
    private String espece;
    private String bassin;
    private String statut;
    private LocalDate dateMiseEnCharge;
    private Integer effectifActuel;
    private BigDecimal totalAchat;
    private BigDecimal totalDistribution;
    private BigDecimal totalPoidsRecolte;
    private BigDecimal coutRevientParKg;
    private BigDecimal coutRevientParIndividu;

    public CoutRevientLotDTO() {
        this.totalAchat = BigDecimal.ZERO;
        this.totalDistribution = BigDecimal.ZERO;
        this.totalPoidsRecolte = BigDecimal.ZERO;
        this.coutRevientParKg = BigDecimal.ZERO;
        this.coutRevientParIndividu = BigDecimal.ZERO;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public String getEspece() {
        return espece;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public String getBassin() {
        return bassin;
    }

    public void setBassin(String bassin) {
        this.bassin = bassin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDateMiseEnCharge() {
        return dateMiseEnCharge;
    }

    public void setDateMiseEnCharge(LocalDate dateMiseEnCharge) {
        this.dateMiseEnCharge = dateMiseEnCharge;
    }

    public Integer getEffectifActuel() {
        return effectifActuel;
    }

    public void setEffectifActuel(Integer effectifActuel) {
        this.effectifActuel = effectifActuel;
    }

    public BigDecimal getTotalAchat() {
        return totalAchat;
    }

    public void setTotalAchat(BigDecimal totalAchat) {
        this.totalAchat = totalAchat;
    }

    public BigDecimal getTotalDistribution() {
        return totalDistribution;
    }

    public void setTotalDistribution(BigDecimal totalDistribution) {
        this.totalDistribution = totalDistribution;
    }

    public BigDecimal getTotalPoidsRecolte() {
        return totalPoidsRecolte;
    }

    public void setTotalPoidsRecolte(BigDecimal totalPoidsRecolte) {
        this.totalPoidsRecolte = totalPoidsRecolte;
    }

    public BigDecimal getCoutRevientParKg() {
        return coutRevientParKg;
    }

    public void setCoutRevientParKg(BigDecimal coutRevientParKg) {
        this.coutRevientParKg = coutRevientParKg;
    }

    public BigDecimal getCoutRevientParIndividu() {
        return coutRevientParIndividu;
    }

    public void setCoutRevientParIndividu(BigDecimal coutRevientParIndividu) {
        this.coutRevientParIndividu = coutRevientParIndividu;
    }
}
