package mg.itu.aquanova.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lot")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "biomasse", precision = 10, scale = 2)
    private BigDecimal biomasse = BigDecimal.ZERO;

    @Column(name = "age_actuel", nullable = false)
    private Integer ageActuel;

    public Lot() {
    }

    public Lot(String nom, BigDecimal biomasse, Integer ageActuel) {
        this.nom = nom;
        this.biomasse = biomasse;
        this.ageActuel = ageActuel;
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

    public BigDecimal getBiomasse() {
        return biomasse;
    }

    public void setBiomasse(BigDecimal biomasse) {
        this.biomasse = biomasse;
    }

    public Integer getAgeActuel() {
        return ageActuel;
    }

    public void setAgeActuel(Integer ageActuel) {
        this.ageActuel = ageActuel;
    }
}