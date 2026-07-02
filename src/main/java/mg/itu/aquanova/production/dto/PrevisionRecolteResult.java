package mg.itu.aquanova.production.dto;

import java.time.LocalDate;

import mg.itu.aquanova.production.models.LotModels;

public class PrevisionRecolteResult {
    private LotModels lot;
    private Double poidsMoyenActuel;
    private Double poidsCible;
    private Double croissanceMoyenneJournaliere;
    private LocalDate dateRecolteEstimee;
    private boolean procheRecolte;
    private String alerte;

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public Double getPoidsMoyenActuel() {
        return poidsMoyenActuel;
    }

    public void setPoidsMoyenActuel(Double poidsMoyenActuel) {
        this.poidsMoyenActuel = poidsMoyenActuel;
    }

    public Double getPoidsCible() {
        return poidsCible;
    }

    public void setPoidsCible(Double poidsCible) {
        this.poidsCible = poidsCible;
    }

    public Double getCroissanceMoyenneJournaliere() {
        return croissanceMoyenneJournaliere;
    }

    public void setCroissanceMoyenneJournaliere(Double croissanceMoyenneJournaliere) {
        this.croissanceMoyenneJournaliere = croissanceMoyenneJournaliere;
    }

    public LocalDate getDateRecolteEstimee() {
        return dateRecolteEstimee;
    }

    public void setDateRecolteEstimee(LocalDate dateRecolteEstimee) {
        this.dateRecolteEstimee = dateRecolteEstimee;
    }

    public boolean isProcheRecolte() {
        return procheRecolte;
    }

    public void setProcheRecolte(boolean procheRecolte) {
        this.procheRecolte = procheRecolte;
    }

    public String getAlerte() {
        return alerte;
    }

    public void setAlerte(String alerte) {
        this.alerte = alerte;
    }
}
