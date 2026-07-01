package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "stade_croissance")
public class StadeCroissanceModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "poids_min", precision = 10, scale = 2)
    private Double poidsMin;

    @Column(name = "poids_max", precision = 10, scale = 2)
    private Double poidsMax;

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

    public Double getPoidsMin() {
        return poidsMin;
    }

    public void setPoidsMin(Double poidsMin) {
        this.poidsMin = poidsMin;
    }

    public Double getPoidsMax() {
        return poidsMax;
    }

    public void setPoidsMax(Double poidsMax) {
        this.poidsMax = poidsMax;
    }
}
