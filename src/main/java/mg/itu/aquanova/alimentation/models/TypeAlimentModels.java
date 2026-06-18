package mg.itu.aquanova.alimentation.models;

import jakarta.persistence.*;

@Entity
@Table(name = "types_aliments")
public class TypeAlimentModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false)
    private String nom;

    public TypeAlimentModels() {
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
}