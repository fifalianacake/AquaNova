package mg.itu.aquanova.dashboard.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mg.itu.aquanova.alerte.models.Alerte;

public class AccueilDashboardDTO {

    private BigDecimal biomasseTotaleKg = BigDecimal.ZERO;
    private BigDecimal variationBiomassePct;

    private long bassinsOccupes;
    private long bassinsTotal;
    private long bassinsLibres;

    private BigDecimal tauxMortalite30j;
    private long mortsSur30j;

    private BigDecimal ventesDuMois = BigDecimal.ZERO;
    private BigDecimal variationVentesPct;

    private long lotsActifs;

    private List<PointSerie> evolutionBiomasse = new ArrayList<>();
    private List<PointSerie> ventesMensuelles = new ArrayList<>();
    private List<PointSerie> repartitionEspeces = new ArrayList<>();

    private List<Alerte> alertesRecentes = new ArrayList<>();
    private long nbAlertesActives;
    private long nbAlertesCritiques;

    public static class PointSerie {
        private final String libelle;
        private final BigDecimal valeur;

        public PointSerie(String libelle, BigDecimal valeur) {
            this.libelle = libelle;
            this.valeur = valeur;
        }

        public String getLibelle() {
            return libelle;
        }

        public BigDecimal getValeur() {
            return valeur;
        }
    }

    public BigDecimal getBiomasseTotaleKg() { return biomasseTotaleKg; }
    public void setBiomasseTotaleKg(BigDecimal v) { this.biomasseTotaleKg = v; }

    public BigDecimal getVariationBiomassePct() { return variationBiomassePct; }
    public void setVariationBiomassePct(BigDecimal v) { this.variationBiomassePct = v; }

    public long getBassinsOccupes() { return bassinsOccupes; }
    public void setBassinsOccupes(long v) { this.bassinsOccupes = v; }

    public long getBassinsTotal() { return bassinsTotal; }
    public void setBassinsTotal(long v) { this.bassinsTotal = v; }

    public long getBassinsLibres() { return bassinsLibres; }
    public void setBassinsLibres(long v) { this.bassinsLibres = v; }

    public BigDecimal getTauxMortalite30j() { return tauxMortalite30j; }
    public void setTauxMortalite30j(BigDecimal v) { this.tauxMortalite30j = v; }

    public long getMortsSur30j() { return mortsSur30j; }
    public void setMortsSur30j(long v) { this.mortsSur30j = v; }

    public BigDecimal getVentesDuMois() { return ventesDuMois; }
    public void setVentesDuMois(BigDecimal v) { this.ventesDuMois = v; }

    public BigDecimal getVariationVentesPct() { return variationVentesPct; }
    public void setVariationVentesPct(BigDecimal v) { this.variationVentesPct = v; }

    public long getLotsActifs() { return lotsActifs; }
    public void setLotsActifs(long v) { this.lotsActifs = v; }

    public List<PointSerie> getEvolutionBiomasse() { return evolutionBiomasse; }
    public void setEvolutionBiomasse(List<PointSerie> v) { this.evolutionBiomasse = v; }

    public List<PointSerie> getVentesMensuelles() { return ventesMensuelles; }
    public void setVentesMensuelles(List<PointSerie> v) { this.ventesMensuelles = v; }

    public List<PointSerie> getRepartitionEspeces() { return repartitionEspeces; }
    public void setRepartitionEspeces(List<PointSerie> v) { this.repartitionEspeces = v; }

    public List<Alerte> getAlertesRecentes() { return alertesRecentes; }
    public void setAlertesRecentes(List<Alerte> v) { this.alertesRecentes = v; }

    public long getNbAlertesActives() { return nbAlertesActives; }
    public void setNbAlertesActives(long v) { this.nbAlertesActives = v; }

    public long getNbAlertesCritiques() { return nbAlertesCritiques; }
    public void setNbAlertesCritiques(long v) { this.nbAlertesCritiques = v; }
}
