package mg.itu.aquanova.sanitaire_equipement.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class TraitementEauFilter {

    private Long id;
    private Long bassinId;
    private Long typeId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    public TraitementEauFilter() {
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

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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
