package mg.itu.aquanova.finance.dto;

import java.math.BigDecimal;

import mg.itu.aquanova.finance.models.StatutRentabilite;

public class RentabiliteLotDTO {

    private Long lotId;
    private String lotCode;
    private String espece;
    private String bassin;
    private String statutLot;

    private BigDecimal chiffreAffaires = BigDecimal.ZERO;

    private BigDecimal coutAlevins = BigDecimal.ZERO;
    private BigDecimal coutAlimentation = BigDecimal.ZERO;
    /** coutAlevins + coutAlimentation. */
    private BigDecimal coutsDirects = BigDecimal.ZERO;

    /** chiffreAffaires − coutsDirects. */
    private BigDecimal margeBrute = BigDecimal.ZERO;

    /** margeBrute / chiffreAffaires × 100. Null si le CA est nul (non calculable). */
    private BigDecimal tauxMargeBrute;

    private BigDecimal poidsVendu = BigDecimal.ZERO;
    /** coutsDirects / poidsVendu. Null si rien n'a été vendu (non calculable). */
    private BigDecimal coutDirectParKgVendu;

    private StatutRentabilite statutRentabilite = StatutRentabilite.NON_CALCULABLE;

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

    public String getStatutLot() {
        return statutLot;
    }

    public void setStatutLot(String statutLot) {
        this.statutLot = statutLot;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getCoutAlevins() {
        return coutAlevins;
    }

    public void setCoutAlevins(BigDecimal coutAlevins) {
        this.coutAlevins = coutAlevins;
    }

    public BigDecimal getCoutAlimentation() {
        return coutAlimentation;
    }

    public void setCoutAlimentation(BigDecimal coutAlimentation) {
        this.coutAlimentation = coutAlimentation;
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

    public BigDecimal getTauxMargeBrute() {
        return tauxMargeBrute;
    }

    public void setTauxMargeBrute(BigDecimal tauxMargeBrute) {
        this.tauxMargeBrute = tauxMargeBrute;
    }

    public BigDecimal getPoidsVendu() {
        return poidsVendu;
    }

    public void setPoidsVendu(BigDecimal poidsVendu) {
        this.poidsVendu = poidsVendu;
    }

    public BigDecimal getCoutDirectParKgVendu() {
        return coutDirectParKgVendu;
    }

    public void setCoutDirectParKgVendu(BigDecimal coutDirectParKgVendu) {
        this.coutDirectParKgVendu = coutDirectParKgVendu;
    }

    public StatutRentabilite getStatutRentabilite() {
        return statutRentabilite;
    }

    public void setStatutRentabilite(StatutRentabilite statutRentabilite) {
        this.statutRentabilite = statutRentabilite;
    }
}
