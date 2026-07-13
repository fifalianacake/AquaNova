package mg.itu.aquanova.sanitaire_equipement.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class ReleveEauFilter {

    private Long id;
    private Long bassinId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private Double temperatureMin;
    private Double temperatureMax;
    private Double phMin;
    private Double phMax;
    private Double oxygeneMin;
    private Double oxygeneMax;

    public ReleveEauFilter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBassinId() {
        return bassinId;
    }

    public void setBassinId(Long bassinId) {
        this.bassinId = bassinId;
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

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public Double getPhMin() {
        return phMin;
    }

    public void setPhMin(Double phMin) {
        this.phMin = phMin;
    }

    public Double getPhMax() {
        return phMax;
    }

    public void setPhMax(Double phMax) {
        this.phMax = phMax;
    }

    public Double getOxygeneMin() {
        return oxygeneMin;
    }

    public void setOxygeneMin(Double oxygeneMin) {
        this.oxygeneMin = oxygeneMin;
    }

    public Double getOxygeneMax() {
        return oxygeneMax;
    }

    public void setOxygeneMax(Double oxygeneMax) {
        this.oxygeneMax = oxygeneMax;
    }
}
