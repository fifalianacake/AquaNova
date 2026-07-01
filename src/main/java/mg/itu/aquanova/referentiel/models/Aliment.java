package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "aliment")
public class Aliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_type_aliment")
    private TypeAlimentModels typeAliment;

    @ManyToOne
    @JoinColumn(name = "id_stade_croissance")
    private StadeCroissanceModels stadeCroissance;

    public Aliment() {
    }

    // getters & setters
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

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeAlimentModels getTypeAliment() {
        return typeAliment;
    }

    public void setTypeAliment(TypeAlimentModels typeAliment) {
        this.typeAliment = typeAliment;
    }

    public StadeCroissanceModels getStadeCroissance() {
        return stadeCroissance;
    }

    public void setStadeCroissance(StadeCroissanceModels stadeCroissance) {
        this.stadeCroissance = stadeCroissance;
    }

}