package mg.itu.aquanova.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceDashboardDTO {

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private BigDecimal chiffreAffaires = BigDecimal.ZERO;

    private BigDecimal coutAlevins = BigDecimal.ZERO;
    private BigDecimal coutAlimentation = BigDecimal.ZERO;
    private BigDecimal coutsDirects = BigDecimal.ZERO;

    private BigDecimal margeBrute = BigDecimal.ZERO;
    private BigDecimal tauxMargeBrute;

    private BigDecimal depenses = BigDecimal.ZERO;
    private BigDecimal profitNet = BigDecimal.ZERO;

    private BigDecimal tauxMargeNette;

    private long nombreVentes;
    private long lotsRentables;
    private long lotsDeficitaires;
    private long lotsNonCalculables;

    private List<LigneMontant> depensesParCategorie = new ArrayList<>();

    private List<FinanceEvolutionDTO> evolution = new ArrayList<>();

    private List<RentabiliteLotDTO> topLotsRentables = new ArrayList<>();
    private List<RentabiliteLotDTO> lotsDeficitairesDetail = new ArrayList<>();

    public static class LigneMontant {
        private final String libelle;
        private final BigDecimal montant;

        public LigneMontant(String libelle, BigDecimal montant) {
            this.libelle = libelle;
            this.montant = montant;
        }

        public String getLibelle() {
            return libelle;
        }

        public BigDecimal getMontant() {
            return montant;
        }
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

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getCoutAlevins() {
        return coutAlevins;
    }

    public void setCoutAlevins(BigDecimal coutAlevins) {
        this.coutAlevins = coutAlevins;
    }

    public BigDecimal getCoutAlimentation() {
        return coutAlimentation;
    }

    public void setCoutAlimentation(BigDecimal coutAlimentation) {
        this.coutAlimentation = coutAlimentation;
    }

    public BigDecimal getCoutsDirects() {
        return coutsDirects;
    }

    public void setCoutsDirects(BigDecimal coutsDirects) {
        this.coutsDirects = coutsDirects;
    }

    public BigDecimal getMargeBrute() {
        return margeBrute;
    }

    public void setMargeBrute(BigDecimal margeBrute) {
        this.margeBrute = margeBrute;
    }

    public BigDecimal getTauxMargeBrute() {
        return tauxMargeBrute;
    }

    public void setTauxMargeBrute(BigDecimal tauxMargeBrute) {
        this.tauxMargeBrute = tauxMargeBrute;
    }

    public BigDecimal getDepenses() {
        return depenses;
    }

    public void setDepenses(BigDecimal depenses) {
        this.depenses = depenses;
    }

    public BigDecimal getProfitNet() {
        return profitNet;
    }

    public void setProfitNet(BigDecimal profitNet) {
        this.profitNet = profitNet;
    }

    public BigDecimal getTauxMargeNette() {
        return tauxMargeNette;
    }

    public void setTauxMargeNette(BigDecimal tauxMargeNette) {
        this.tauxMargeNette = tauxMargeNette;
    }

    public long getNombreVentes() {
        return nombreVentes;
    }

    public void setNombreVentes(long nombreVentes) {
        this.nombreVentes = nombreVentes;
    }

    public long getLotsRentables() {
        return lotsRentables;
    }

    public void setLotsRentables(long lotsRentables) {
        this.lotsRentables = lotsRentables;
    }

    public long getLotsDeficitaires() {
        return lotsDeficitaires;
    }

    public void setLotsDeficitaires(long lotsDeficitaires) {
        this.lotsDeficitaires = lotsDeficitaires;
    }

    public long getLotsNonCalculables() {
        return lotsNonCalculables;
    }

    public void setLotsNonCalculables(long lotsNonCalculables) {
        this.lotsNonCalculables = lotsNonCalculables;
    }

    public List<LigneMontant> getDepensesParCategorie() {
        return depensesParCategorie;
    }

    public void setDepensesParCategorie(List<LigneMontant> depensesParCategorie) {
        this.depensesParCategorie = depensesParCategorie;
    }

    public List<FinanceEvolutionDTO> getEvolution() {
        return evolution;
    }

    public void setEvolution(List<FinanceEvolutionDTO> evolution) {
        this.evolution = evolution;
    }

    public List<RentabiliteLotDTO> getTopLotsRentables() {
        return topLotsRentables;
    }

    public void setTopLotsRentables(List<RentabiliteLotDTO> topLotsRentables) {
        this.topLotsRentables = topLotsRentables;
    }

    public List<RentabiliteLotDTO> getLotsDeficitairesDetail() {
        return lotsDeficitairesDetail;
    }

    public void setLotsDeficitairesDetail(List<RentabiliteLotDTO> lotsDeficitairesDetail) {
        this.lotsDeficitairesDetail = lotsDeficitairesDetail;
    }
}
