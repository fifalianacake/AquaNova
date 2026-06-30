package mg.itu.aquanova.alimentation.dto;

public class StockDTO {

    private Long id;
    private String nom;
    private Double stock;
    private Double seuil;

    public StockDTO(Long id, String nom, Double stock, Double seuil) {
        this.id = id;
        this.nom = nom;
        this.stock = stock;
        this.seuil = seuil;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public Double getStock() { return stock; }
    public Double getSeuil() { return seuil; }

    public boolean isAlerte() {
        return seuil != null && stock < seuil;
    }
}