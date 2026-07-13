package mg.itu.aquanova.vente.dto;

public class VenteStatsDto {

    private Double chiffreAffaires;
    private Double volumeEcoule;
    private Long nombreVentes;
    private Double prixMoyenKg;

    public VenteStatsDto() {}

    public Double getChiffreAffaires() { return chiffreAffaires; }
    public void setChiffreAffaires(Double chiffreAffaires) { this.chiffreAffaires = chiffreAffaires; }

    public Double getVolumeEcoule() { return volumeEcoule; }
    public void setVolumeEcoule(Double volumeEcoule) { this.volumeEcoule = volumeEcoule; }

    public Long getNombreVentes() { return nombreVentes; }
    public void setNombreVentes(Long nombreVentes) { this.nombreVentes = nombreVentes; }

    public Double getPrixMoyenKg() { return prixMoyenKg; }
    public void setPrixMoyenKg(Double prixMoyenKg) { this.prixMoyenKg = prixMoyenKg; }
}