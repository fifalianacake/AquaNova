package mg.itu.aquanova.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "aliment")
public class Aliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAliment type;

    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    @Column(name = "taille_granule")
    private BigDecimal tailleGranule;

    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;

    @Column(name = "seuil_alerte_kg", nullable = false)
    private BigDecimal seuilAlerteKg;

    public Aliment() {
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

    public TypeAliment getType() {
        return type;
    }

    public void setType(TypeAliment type) {
        this.type = type;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public BigDecimal getTailleGranule() {
        return tailleGranule;
    }

    public void setTailleGranule(BigDecimal tailleGranule) {
        this.tailleGranule = tailleGranule;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getSeuilAlerteKg() {
        return seuilAlerteKg;
    }

    public void setSeuilAlerteKg(BigDecimal seuilAlerteKg) {
        this.seuilAlerteKg = seuilAlerteKg;
    }
}
