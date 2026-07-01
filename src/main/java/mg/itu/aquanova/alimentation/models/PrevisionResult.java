package mg.itu.aquanova.alimentation.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrevisionResult {

    private Long alimentId;
    private String alimentNom;
    private BigDecimal stockRestant;
    private BigDecimal consommationParJour;
    private Long joursRestants;
    private LocalDate dateRupture;
    private BigDecimal quantiteSuggereePurchase;
    private String alerte;

    public PrevisionResult() {}

    public PrevisionResult(Long alimentId, String alimentNom, BigDecimal stockRestant,BigDecimal consommationParJour, Long joursRestants,LocalDate dateRupture, BigDecimal quantiteSuggereePurchase) {
        this.alimentId = alimentId;
        this.alimentNom = alimentNom;
        this.stockRestant = stockRestant;
        this.consommationParJour = consommationParJour;
        this.joursRestants = joursRestants;
        this.dateRupture = dateRupture;
        this.quantiteSuggereePurchase = quantiteSuggereePurchase;
    }

    public Long getAlimentId() { return alimentId; }
    public void setAlimentId(Long alimentId) { this.alimentId = alimentId; }

    public String getAlimentNom() { return alimentNom; }
    public void setAlimentNom(String alimentNom) { this.alimentNom = alimentNom; }

    public BigDecimal getStockRestant() { return stockRestant; }
    public void setStockRestant(BigDecimal stockRestant) { this.stockRestant = stockRestant; }

    public BigDecimal getConsommationParJour() { return consommationParJour; }
    public void setConsommationParJour(BigDecimal consommationParJour) { this.consommationParJour = consommationParJour; }

    public Long getJoursRestants() { return joursRestants; }
    public void setJoursRestants(Long joursRestants) { this.joursRestants = joursRestants; }

    public LocalDate getDateRupture() { return dateRupture; }
    public void setDateRupture(LocalDate dateRupture) { this.dateRupture = dateRupture; }

    public BigDecimal getQuantiteSuggereePurchase() { return quantiteSuggereePurchase; }
    public void setQuantiteSuggereePurchase(BigDecimal quantiteSuggereePurchase) { this.quantiteSuggereePurchase = quantiteSuggereePurchase; }

    public BigDecimal getQuantiteRestante() { return stockRestant; }
    public void setQuantiteRestante(BigDecimal quantiteRestante) { this.stockRestant = quantiteRestante; }

    public BigDecimal getConsommationJour() { return consommationParJour; }
    public void setConsommationJour(BigDecimal consommationJour) { this.consommationParJour = consommationJour; }

    public BigDecimal getSuggestionQuantiteAchat() { return quantiteSuggereePurchase; }
    public void setSuggestionQuantiteAchat(BigDecimal suggestionQuantiteAchat) { this.quantiteSuggereePurchase = suggestionQuantiteAchat; }

    public String getAlerte() { return alerte; }
    public void setAlerte(String alerte) { this.alerte = alerte; }
}
