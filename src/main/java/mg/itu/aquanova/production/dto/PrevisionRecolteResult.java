package mg.itu.aquanova.production.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import mg.itu.aquanova.production.models.LotModels;

public class PrevisionRecolteResult {
    private LotModels lot;
    private BigDecimal poidsMoyenActuel;
    private BigDecimal poidsCible;
    private BigDecimal croissanceMoyenneJournaliere;
    private LocalDate dateRecolteEstimee;
    private boolean procheRecolte;
    private String alerte;

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public BigDecimal getPoidsMoyenActuel() {
        return poidsMoyenActuel;
    }

    public void setPoidsMoyenActuel(BigDecimal poidsMoyenActuel) {
        this.poidsMoyenActuel = poidsMoyenActuel;
    }

    public BigDecimal getPoidsCible() {
        return poidsCible;
    }

    public void setPoidsCible(BigDecimal poidsCible) {
        this.poidsCible = poidsCible;
    }

    public BigDecimal getCroissanceMoyenneJournaliere() {
        return croissanceMoyenneJournaliere;
    }

    public void setCroissanceMoyenneJournaliere(BigDecimal croissanceMoyenneJournaliere) {
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
