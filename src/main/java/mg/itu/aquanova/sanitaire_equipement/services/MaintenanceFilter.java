package mg.itu.aquanova.sanitaire_equipement.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import mg.itu.aquanova.sanitaire_equipement.models.StatutInterventionEnum;

public class MaintenanceFilter {
    private Long id;
    private Integer idEquipement;
    private Integer idUser;
    private Integer idCategorieMaintenance;
    private LocalDate debutDateMaintenance;   
    private LocalDate finDateMaintenance;
    private BigDecimal cout;
    private StatutInterventionEnum statutIntervention;
    private LocalDate debutDateResolution;
    private LocalDate finDateResolution;

    public MaintenanceFilter() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(Integer idEquipement) {
        this.idEquipement = idEquipement;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdCategorieMaintenance() {
        return idCategorieMaintenance;
    }

    public void setIdCategorieMaintenance(Integer idCategorieMaintenance) {
        this.idCategorieMaintenance = idCategorieMaintenance;
    }

    public LocalDate getDebutDateMaintenance() {
        return debutDateMaintenance;
    }

    public void setDebutDateMaintenance(LocalDate debutDateMaintenance) {
        this.debutDateMaintenance = debutDateMaintenance;
    }

    public LocalDate getFinDateMaintenance() {
        return finDateMaintenance;
    }

    public void setFinDateMaintenance(LocalDate finDateMaintenance) {
        this.finDateMaintenance = finDateMaintenance;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public StatutInterventionEnum getStatutIntervention() {
        return statutIntervention;
    }

    public void setStatutIntervention(StatutInterventionEnum statutIntervention) {
        this.statutIntervention = statutIntervention;
    }

    public LocalDate getDebutDateResolution() {
        return debutDateResolution;
    }

    public void setDebutDateResolution(LocalDate debutDateResolution) {
        this.debutDateResolution = debutDateResolution;
    }

    public LocalDate getFinDateResolution() {
        return finDateResolution;
    }

    public void setFinDateResolution(LocalDate finDateResolution) {
        this.finDateResolution = finDateResolution;
    }

    
}
