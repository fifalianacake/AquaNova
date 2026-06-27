package mg.itu.aquanova.production.services;

import java.time.LocalDate;

import mg.itu.aquanova.production.models.TypeEvenementLot;

public class JournalLotFilter {
    private Long lotId;
    private TypeEvenementLot.LibelleEvenement typeEvenement;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public TypeEvenementLot.LibelleEvenement getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(TypeEvenementLot.LibelleEvenement typeEvenement) {
        this.typeEvenement = typeEvenement;
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
