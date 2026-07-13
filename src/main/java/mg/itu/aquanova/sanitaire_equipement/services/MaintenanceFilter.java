package mg.itu.aquanova.sanitaire_equipement.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class MaintenanceFilter {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private BigDecimal coutMin;
    private BigDecimal coutMax;

    public MaintenanceFilter() {
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

    public BigDecimal getCoutMin() {
        return coutMin;
    }

    public void setCoutMin(BigDecimal coutMin) {
        this.coutMin = coutMin;
    }

    public BigDecimal getCoutMax() {
        return coutMax;
    }

    public void setCoutMax(BigDecimal coutMax) {
        this.coutMax = coutMax;
    }
}
