package mg.itu.aquanova.finance.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Résultat d'une simulation : la marge attendue pour chaque date de récolte possible. */
public class SimulationRecolteDTO {

    private Long lotId;
    private String codeLot;
    private String espece;
    private String bassin;

    /** Hypothèses de la simulation (modifiables par l'utilisateur). */
    private Double prixVenteKg;
    private Double coutAlimentKg;
    private Double tauxMortaliteJournalier;   // en % par jour
    private int horizonJours;

    /** Données du lot au jour de la simulation. */
    private Integer effectifActuel;
    private Double poidsMoyenActuel;
    private Double poidsCible;
    private Double croissanceJournaliere;     // g/jour ; null si non calculable
    private Double coutsDejaEngages;          // alevins + aliment déjà distribué

    private final List<PointSimulation> points = new ArrayList<>();

    /** Résultats. */
    private PointSimulation optimum;
    private PointSimulation aujourdHui;
    private LocalDate dateCiblePoids;         // date où le poids cible est atteint
    private String impossible;                // motif si la simulation n'est pas calculable

    public boolean isCalculable() {
        return impossible == null;
    }

    /** Gain (ou perte) qu'apporte le fait d'attendre la date optimale plutôt que de récolter aujourd'hui. */
    public Double getGainAAttendre() {
        if (optimum == null || aujourdHui == null) {
            return null;
        }
        return optimum.getMarge() - aujourdHui.getMarge();
    }

    public static class PointSimulation {
        private final int jour;
        private final LocalDate date;
        private final Double poidsMoyen;      // g
        private final Integer effectif;
        private final Double biomasse;        // kg
        private final Double chiffreAffaires;
        private final Double coutAlimentFutur;
        private final Double marge;

        public PointSimulation(int jour, LocalDate date, Double poidsMoyen, Integer effectif,
                               Double biomasse, Double chiffreAffaires, Double coutAlimentFutur, Double marge) {
            this.jour = jour;
            this.date = date;
            this.poidsMoyen = poidsMoyen;
            this.effectif = effectif;
            this.biomasse = biomasse;
            this.chiffreAffaires = chiffreAffaires;
            this.coutAlimentFutur = coutAlimentFutur;
            this.marge = marge;
        }

        public int getJour() { return jour; }
        public LocalDate getDate() { return date; }
        public Double getPoidsMoyen() { return poidsMoyen; }
        public Integer getEffectif() { return effectif; }
        public Double getBiomasse() { return biomasse; }
        public Double getChiffreAffaires() { return chiffreAffaires; }
        public Double getCoutAlimentFutur() { return coutAlimentFutur; }
        public Double getMarge() { return marge; }
    }

    // ------------------------------------------------------------------ accesseurs
    public Long getLotId() { return lotId; }
    public void setLotId(Long lotId) { this.lotId = lotId; }

    public String getCodeLot() { return codeLot; }
    public void setCodeLot(String codeLot) { this.codeLot = codeLot; }

    public String getEspece() { return espece; }
    public void setEspece(String espece) { this.espece = espece; }

    public String getBassin() { return bassin; }
    public void setBassin(String bassin) { this.bassin = bassin; }

    public Double getPrixVenteKg() { return prixVenteKg; }
    public void setPrixVenteKg(Double prixVenteKg) { this.prixVenteKg = prixVenteKg; }

    public Double getCoutAlimentKg() { return coutAlimentKg; }
    public void setCoutAlimentKg(Double coutAlimentKg) { this.coutAlimentKg = coutAlimentKg; }

    public Double getTauxMortaliteJournalier() { return tauxMortaliteJournalier; }
    public void setTauxMortaliteJournalier(Double taux) { this.tauxMortaliteJournalier = taux; }

    public int getHorizonJours() { return horizonJours; }
    public void setHorizonJours(int horizonJours) { this.horizonJours = horizonJours; }

    public Integer getEffectifActuel() { return effectifActuel; }
    public void setEffectifActuel(Integer effectifActuel) { this.effectifActuel = effectifActuel; }

    public Double getPoidsMoyenActuel() { return poidsMoyenActuel; }
    public void setPoidsMoyenActuel(Double poidsMoyenActuel) { this.poidsMoyenActuel = poidsMoyenActuel; }

    public Double getPoidsCible() { return poidsCible; }
    public void setPoidsCible(Double poidsCible) { this.poidsCible = poidsCible; }

    public Double getCroissanceJournaliere() { return croissanceJournaliere; }
    public void setCroissanceJournaliere(Double croissance) { this.croissanceJournaliere = croissance; }

    public Double getCoutsDejaEngages() { return coutsDejaEngages; }
    public void setCoutsDejaEngages(Double coutsDejaEngages) { this.coutsDejaEngages = coutsDejaEngages; }

    public List<PointSimulation> getPoints() { return points; }

    public PointSimulation getOptimum() { return optimum; }
    public void setOptimum(PointSimulation optimum) { this.optimum = optimum; }

    public PointSimulation getAujourdHui() { return aujourdHui; }
    public void setAujourdHui(PointSimulation aujourdHui) { this.aujourdHui = aujourdHui; }

    public LocalDate getDateCiblePoids() { return dateCiblePoids; }
    public void setDateCiblePoids(LocalDate dateCiblePoids) { this.dateCiblePoids = dateCiblePoids; }

    public String getImpossible() { return impossible; }
    public void setImpossible(String impossible) { this.impossible = impossible; }
}
