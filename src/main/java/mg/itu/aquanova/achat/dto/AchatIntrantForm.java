package mg.itu.aquanova.achat.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class AchatIntrantForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateAchat = LocalDate.now();

    private Long fournisseurId;
    private Long categorieDepenseId;
    private Long intrantId;
    private BigDecimal quantite;
    private String unite;
    private BigDecimal prixUnitaire;
    private String referenceFacture;
    private String observation;
    private boolean validerDirectement;

    public AchatIntrantForm() {
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

    public Long getIntrantId() {
        return intrantId;
    }

    public void setIntrantId(Long intrantId) {
        this.intrantId = intrantId;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
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
