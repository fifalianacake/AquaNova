package mg.itu.aquanova.vente.dto;

public class PerformanceClientDto {

    private String clientNom;       // String car client est un String dans Vente
    private Long nombreVentes;
    private Double volumeAchete;
    private Double chiffreAffaires;

    public PerformanceClientDto() {}

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public Long getNombreVentes() { return nombreVentes; }
    public void setNombreVentes(Long nombreVentes) { this.nombreVentes = nombreVentes; }

    public Double getVolumeAchete() { return volumeAchete; }
    public void setVolumeAchete(Double volumeAchete) { this.volumeAchete = volumeAchete; }

    public Double getChiffreAffaires() { return chiffreAffaires; }
    public void setChiffreAffaires(Double chiffreAffaires) { this.chiffreAffaires = chiffreAffaires; }
}