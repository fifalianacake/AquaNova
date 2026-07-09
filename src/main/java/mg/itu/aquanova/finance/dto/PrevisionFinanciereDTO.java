package mg.itu.aquanova.finance.dto;

public class PrevisionFinanciereDTO {
    private Long lotId;
    private String codeLot;
    private String espece;
    private Double biomassePrevue; // Estimation du poids total
    private Double prixMoyenVenteKg; // Prix estimé au kg
    private Double caPrevisionnel; // Biomasse * Prix moyen
    private Double coutPrevisionnel; // Dépenses totales estimées
    private Double profitPrevisionnel; // CA - Coût
    private Double margePrevisionnelle;// En % (Profit / CA * 100)

    public PrevisionFinanciereDTO() {
    }

    public PrevisionFinanciereDTO(Long lotId, String codeLot, String espece, Double biomassePrevue,
            Double prixMoyenVenteKg, Double caPrevisionnel, Double coutPrevisionnel, Double profitPrevisionnel,
            Double margePrevisionnelle) {
        this.lotId = lotId;
        this.codeLot = codeLot;
        this.espece = espece;
        this.biomassePrevue = biomassePrevue;
        this.prixMoyenVenteKg = prixMoyenVenteKg;
        this.caPrevisionnel = caPrevisionnel;
        this.coutPrevisionnel = coutPrevisionnel;
        this.profitPrevisionnel = profitPrevisionnel;
        this.margePrevisionnelle = margePrevisionnelle;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public String getEspece() {
        return espece;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public Double getBiomassePrevue() {
        return biomassePrevue;
    }

    public void setBiomassePrevue(Double biomassePrevue) {
        this.biomassePrevue = biomassePrevue;
    }

    public Double getPrixMoyenVenteKg() {
        return prixMoyenVenteKg;
    }

    public void setPrixMoyenVenteKg(Double prixMoyenVenteKg) {
        this.prixMoyenVenteKg = prixMoyenVenteKg;
    }

    public Double getCaPrevisionnel() {
        return caPrevisionnel;
    }

    public void setCaPrevisionnel(Double caPrevisionnel) {
        this.caPrevisionnel = caPrevisionnel;
    }

    public Double getCoutPrevisionnel() {
        return coutPrevisionnel;
    }

    public void setCoutPrevisionnel(Double coutPrevisionnel) {
        this.coutPrevisionnel = coutPrevisionnel;
    }

    public Double getProfitPrevisionnel() {
        return profitPrevisionnel;
    }

    public void setProfitPrevisionnel(Double profitPrevisionnel) {
        this.profitPrevisionnel = profitPrevisionnel;
    }

    public Double getMargePrevisionnelle() {
        return margePrevisionnelle;
    }

    public void setMargePrevisionnelle(Double margePrevisionnelle) {
        this.margePrevisionnelle = margePrevisionnelle;
    }

    public String getCodeLot() {
        return codeLot;
    }

    public void setCodeLot(String codeLot) {
        this.codeLot = codeLot;
    }
}
