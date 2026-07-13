package mg.itu.aquanova.alimentation.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import mg.itu.aquanova.alimentation.models.TypeMouvement;

public class MouvementFilter {

    private Long id;
    private TypeMouvement type;
    private Long alimentId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    public MouvementFilter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeMouvement getType() {
        return type;
    }

    public void setType(TypeMouvement type) {
        this.type = type;
    }

    public Long getAlimentId() {
        return alimentId;
    }

    public void setAlimentId(Long alimentId) {
        this.alimentId = alimentId;
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
