package mg.itu.aquanova.achat.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class AchatAlevinForm {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateAchat = LocalDate.now();

    private Long fournisseurId;
    private Long categorieDepenseId;
    private Integer especeId;
    private Integer effectif;
    private BigDecimal poidsMoyen;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private String referenceFacture;
    private Long lotId;
    private Long bassinId;
    private boolean validerDirectement;

    // getters and setters
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
    public Integer getEspeceId() {
        return especeId;
    }
    public void setEspeceId(Integer especeId) {
        this.especeId = especeId;
    }
    public Integer getEffectif() {
        return effectif;
    }
    public void setEffectif(Integer effectif) {
        this.effectif = effectif;
    }
    public BigDecimal getPoidsMoyen() {
        return poidsMoyen;
    }
    public void setPoidsMoyen(BigDecimal poidsMoyen) {
        this.poidsMoyen = poidsMoyen;
    }
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    public String getReferenceFacture() {
        return referenceFacture;
    }
    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }
    public Long getLotId() {
        return lotId;
    }
    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }
    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    public Long getBassinId() {
        return bassinId;
    }
    public void setBassinId(Long bassinId) {
        this.bassinId = bassinId;
    }
    public boolean isValiderDirectement() {
        return validerDirectement;
    }
    public void setValiderDirectement(boolean validerDirectement) {
        this.validerDirectement = validerDirectement;
    }
}
