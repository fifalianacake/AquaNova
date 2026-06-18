package mg.itu.aquanova.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lot")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private BigDecimal biomasse;

    @Column(name = "age_actuel", nullable = false)
    private Integer ageActuel;

    public Lot() {
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public BigDecimal getBiomasse() {
        return biomasse;
    }

    public Integer getAgeActuel() {
        return ageActuel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setBiomasse(BigDecimal biomasse) {
        this.biomasse = biomasse;
    }

    public void setAgeActuel(Integer ageActuel) {
        this.ageActuel = ageActuel;
    }
}
