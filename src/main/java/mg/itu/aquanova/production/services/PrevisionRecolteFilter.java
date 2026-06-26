package mg.itu.aquanova.production.services;

import java.time.LocalDate;

public class PrevisionRecolteFilter {
    private Long lotId;
    private Integer especeId;
    private Long bassinId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Integer getEspeceId() {
        return especeId;
    }

    public void setEspeceId(Integer especeId) {
        this.especeId = especeId;
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
}
