package mg.itu.aquanova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "aliment")
public class Aliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // Alevin / Croissance / Finition

    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    @Column(name = "taille_granule", precision = 5, scale = 2)
    private BigDecimal tailleGranule;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "seuil_alerte_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal seuilAlerteKg;

    @OneToMany(mappedBy = "aliment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MouvementStock> mouvements;

    public Aliment() {
    }

    public Aliment(String nom, String type, Integer ageMin, Integer ageMax,
            BigDecimal tailleGranule, BigDecimal prixUnitaire, BigDecimal seuilAlerteKg) {
        this.nom = nom;
        this.type = type;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.tailleGranule = tailleGranule;
        this.prixUnitaire = prixUnitaire;
        this.seuilAlerteKg = seuilAlerteKg;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public List<MouvementStock> getMouvements() {
        return mouvements;
    }

    public void setMouvements(List<MouvementStock> mouvements) {
        this.mouvements = mouvements;
    }
}