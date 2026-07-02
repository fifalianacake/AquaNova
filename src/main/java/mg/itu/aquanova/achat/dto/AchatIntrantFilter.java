package mg.itu.aquanova.achat.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import mg.itu.aquanova.achat.models.StatutAchat;

public class AchatIntrantFilter {

    private Long id;
    private Long fournisseurId;
    private Long categorieDepenseId;
    private Long intrantId;
    private StatutAchat statutAchat;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private String referenceFacture;

    public AchatIntrantFilter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatutAchat getStatutAchat() {
        return statutAchat;
    }

    public void setStatutAchat(StatutAchat statutAchat) {
        this.statutAchat = statutAchat;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getMontantMin() {
        return montantMin;
    }

    public void setMontantMin(BigDecimal montantMin) {
        this.montantMin = montantMin;
    }

    public BigDecimal getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(BigDecimal montantMax) {
        this.montantMax = montantMax;
    }

    public String getReferenceFacture() {
        return referenceFacture;
    }

    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }
}
