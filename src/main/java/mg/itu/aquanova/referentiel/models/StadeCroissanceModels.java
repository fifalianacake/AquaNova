package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stade_croissance")
public class StadeCroissanceModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "poids_min", precision = 10, scale = 2)
    private BigDecimal poidsMin;

    @Column(name = "poids_max", precision = 10, scale = 2)
    private BigDecimal poidsMax;

    public StadeCroissanceModels() {
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

    public BigDecimal getPoidsMin() {
        return poidsMin;
    }

    public void setPoidsMin(BigDecimal poidsMin) {
        this.poidsMin = poidsMin;
    }

    public BigDecimal getPoidsMax() {
        return poidsMax;
    }

    public void setPoidsMax(BigDecimal poidsMax) {
        this.poidsMax = poidsMax;
    }

    public boolean correspondAuPoids(BigDecimal poids) {
        if (poids == null) {
            return false;
        }
        boolean auDessusMin = poidsMin == null || poids.compareTo(poidsMin) >= 0;
        boolean sousLeMax = poidsMax == null || poids.compareTo(poidsMax) < 0;
        return auDessusMin && sousLeMax;
    }
}
