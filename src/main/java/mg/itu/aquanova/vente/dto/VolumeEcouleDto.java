package mg.itu.aquanova.vente.dto;

public class VolumeEcouleDto {

    private String lotNom;
    private String recolteReference;
    private Double poidsVendu;
    private Long effectifVendu;
    private Double montantTotal;

    public VolumeEcouleDto() {}

    public String getLotNom() { return lotNom; }
    public void setLotNom(String lotNom) { this.lotNom = lotNom; }

    public String getRecolteReference() { return recolteReference; }
    public void setRecolteReference(String recolteReference) { this.recolteReference = recolteReference; }

    public Double getPoidsVendu() { return poidsVendu; }
    public void setPoidsVendu(Double poidsVendu) { this.poidsVendu = poidsVendu; }

    public Long getEffectifVendu() { return effectifVendu; }
    public void setEffectifVendu(Long effectifVendu) { this.effectifVendu = effectifVendu; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
}