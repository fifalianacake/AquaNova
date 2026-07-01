package mg.itu.aquanova.alimentation.models;

import java.time.LocalDate;

public class PrevisionResult {

    private Long alimentId;
    private String alimentNom;
    private Double stockRestant;
    private Double consommationParJour;
    private Long joursRestants;
    private LocalDate dateRupture;
    private Double quantiteSuggereePurchase;
    private String alerte;

    public PrevisionResult() {}

    public PrevisionResult(Long alimentId, String alimentNom, Double stockRestant, Double consommationParJour, Long joursRestants, LocalDate dateRupture, Double quantiteSuggereePurchase) {
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

    public Double getStockRestant() { return stockRestant; }
    public void setStockRestant(Double stockRestant) { this.stockRestant = stockRestant; }

    public Double getConsommationParJour() { return consommationParJour; }
    public void setConsommationParJour(Double consommationParJour) { this.consommationParJour = consommationParJour; }

    public Long getJoursRestants() { return joursRestants; }
    public void setJoursRestants(Long joursRestants) { this.joursRestants = joursRestants; }

    public LocalDate getDateRupture() { return dateRupture; }
    public void setDateRupture(LocalDate dateRupture) { this.dateRupture = dateRupture; }

    public Double getQuantiteSuggereePurchase() { return quantiteSuggereePurchase; }
    public void setQuantiteSuggereePurchase(Double quantiteSuggereePurchase) { this.quantiteSuggereePurchase = quantiteSuggereePurchase; }

    public Double getQuantiteRestante() { return stockRestant; }
    public void setQuantiteRestante(Double quantiteRestante) { this.stockRestant = quantiteRestante; }

    public Double getConsommationJour() { return consommationParJour; }
    public void setConsommationJour(Double consommationJour) { this.consommationParJour = consommationJour; }

    public Double getSuggestionQuantiteAchat() { return quantiteSuggereePurchase; }
    public void setSuggestionQuantiteAchat(Double suggestionQuantiteAchat) { this.quantiteSuggereePurchase = suggestionQuantiteAchat; }

    public String getAlerte() { return alerte; }
    public void setAlerte(String alerte) { this.alerte = alerte; }
}
