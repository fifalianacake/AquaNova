package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "intrant")
public class Intrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie_intrant", nullable = false, length = 40)
    private CategorieIntrant categorieIntrant;

    @Column(nullable = false, length = 30)
    private String unite;

    @Column(name = "prix_reference", precision = 14, scale = 2)
    private BigDecimal prixReference;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean actif = true;

    public Intrant() {
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

    public BigDecimal getPrixReference() {
        return prixReference;
    }

    public void setPrixReference(BigDecimal prixReference) {
        this.prixReference = prixReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
