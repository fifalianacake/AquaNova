package mg.itu.aquanova.production.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import mg.itu.aquanova.production.models.StatutRecolteEnum;

public class RecolteFilter {

    private Long lotId;
    private Long typeRecolteId;
    private StatutRecolteEnum statut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    public RecolteFilter() {
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Long getTypeRecolteId() {
        return typeRecolteId;
    }

    public void setTypeRecolteId(Long typeRecolteId) {
        this.typeRecolteId = typeRecolteId;
    }

    public StatutRecolteEnum getStatut() {
        return statut;
    }

    public void setStatut(StatutRecolteEnum statut) {
        this.statut = statut;
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
}
