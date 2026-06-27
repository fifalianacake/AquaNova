package mg.itu.aquanova.alimentation.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aliments")
public class Aliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "age_min_recommande", nullable = false)
    private Integer ageMinRecommande;

    @Column(name = "age_max_recommande", nullable = false)
    private Integer ageMaxRecommande;

    @Column(name = "taille_granules", precision = 10, scale = 2)
    private BigDecimal tailleGranules;

    @Column(name = "prix_unitaire", precision = 10, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;

    @Column(name = "stock_actuel", precision = 10, scale = 2, nullable = false)
    private BigDecimal stockActuel = BigDecimal.ZERO;

    public Aliment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAgeMinRecommande() {
        return ageMinRecommande;
    }

    public void setAgeMinRecommande(Integer ageMinRecommande) {
        this.ageMinRecommande = ageMinRecommande;
    }

    public Integer getAgeMaxRecommande() {
        return ageMaxRecommande;
    }

    public void setAgeMaxRecommande(Integer ageMaxRecommande) {
        this.ageMaxRecommande = ageMaxRecommande;
    }

    public BigDecimal getTailleGranules() {
        return tailleGranules;
    }

    public void setTailleGranules(BigDecimal tailleGranules) {
        this.tailleGranules = tailleGranules;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }
}
