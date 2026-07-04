package mg.itu.aquanova.vente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionFilterDTO {

    private Long id;
    private String client;
    private String typeClient;
    private Long idRecolte;
    private Long idLot;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;
    private Long statutId;
    private BigDecimal montantMin;
    private BigDecimal montantMax;

    public TransactionFilterDTO() {
    }

    public TransactionFilterDTO(String client, String typeClient, 
        Long idRecolte, Long idLot, LocalDate dateDebut, LocalDate dateFin, 
        String statut, BigDecimal montantMin, BigDecimal montantMax, Long id, Long statutId) {
        this.client = client;
        this.typeClient = typeClient;
        this.idRecolte = idRecolte;
        this.idLot = idLot;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.montantMin = montantMin;
        this.montantMax = montantMax;
        this.id = id;
        this.statutId = statutId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTypeClient() {
        return typeClient;
    }

    public void setTypeClient(String typeClient) {
        this.typeClient = typeClient;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
