package mg.itu.aquanova.achat.dto;

import mg.itu.aquanova.achat.models.CategorieIntrant;

public class IntrantFilter {

    private Long id;
    private String nom;
    private CategorieIntrant categorieIntrant;
    private String unite;
    private Boolean actif;

    public IntrantFilter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public CategorieIntrant getCategorieIntrant() {
        return categorieIntrant;
    }

    public void setCategorieIntrant(CategorieIntrant categorieIntrant) {
        this.categorieIntrant = categorieIntrant;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
