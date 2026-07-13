package mg.itu.aquanova.vente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionFilterDTO {

    private Long id;
    private String client;
    private Long idRecolte;
    private Long idLot;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long statutId;
    private BigDecimal montantMin;
    private BigDecimal montantMax;

    public TransactionFilterDTO() {
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Long getIdRecolte() {
        return idRecolte;
    }

    public void setIdRecolte(Long idRecolte) {
        this.idRecolte = idRecolte;
    }

    public Long getIdLot() {
        return idLot;
    }

    public void setIdLot(Long idLot) {
        this.idLot = idLot;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatutId() {
        return statutId;
    }

    public void setStatutId(Long statutId) {
        this.statutId = statutId;
    }

}
