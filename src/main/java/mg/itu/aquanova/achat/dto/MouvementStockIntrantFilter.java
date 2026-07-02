package mg.itu.aquanova.achat.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import mg.itu.aquanova.achat.models.TypeMouvementIntrant;

public class MouvementStockIntrantFilter {

    private Long id;
    private Long intrantId;
    private TypeMouvementIntrant typeMouvement;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    public MouvementStockIntrantFilter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIntrantId() {
        return intrantId;
    }

    public void setIntrantId(Long intrantId) {
        this.intrantId = intrantId;
    }

    public TypeMouvementIntrant getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvementIntrant typeMouvement) {
        this.typeMouvement = typeMouvement;
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
