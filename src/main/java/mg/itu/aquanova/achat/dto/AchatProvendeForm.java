package mg.itu.aquanova.achat.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class AchatProvendeForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateAchat = LocalDate.now();

    private Long fournisseurId;
    private Long categorieDepenseId;
    private Long alimentId;
    private BigDecimal quantite;
    private BigDecimal prixUnitaire;
    private String referenceFacture;
    private String observation;
    private boolean validerDirectement;

    public AchatProvendeForm() {
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Long getCategorieDepenseId() {
        return categorieDepenseId;
    }

    public void setCategorieDepenseId(Long categorieDepenseId) {
        this.categorieDepenseId = categorieDepenseId;
    }

    public Long getAlimentId() {
        return alimentId;
    }

    public void setAlimentId(Long alimentId) {
        this.alimentId = alimentId;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getReferenceFacture() {
        return referenceFacture;
    }

    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public boolean isValiderDirectement() {
        return validerDirectement;
    }

    public void setValiderDirectement(boolean validerDirectement) {
        this.validerDirectement = validerDirectement;
    }
}
