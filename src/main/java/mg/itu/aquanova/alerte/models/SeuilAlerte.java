package mg.itu.aquanova.alerte.models;

import jakarta.persistence.*;

@Entity
@Table(name = "seuil_alerte")
public class SeuilAlerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "module_source", nullable = false)
    private String moduleSource;

    @Column(nullable = false)
    private Double valeur;

    private String unite;

    private String description;

    @Column(nullable = false)
    private Boolean actif = true;

    // Constructeur vide obligatoire pour JPA
    public SeuilAlerte() {
    }

    // Constructeur complet
    public SeuilAlerte(Long id, String code, String libelle, String moduleSource, Double valeur, String unite, String description, Boolean actif) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.moduleSource = moduleSource;
        this.valeur = valeur;
        this.unite = unite;
        this.description = description;
        this.actif = actif;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getModuleSource() {
        return moduleSource;
    }

    public void setModuleSource(String moduleSource) {
        this.moduleSource = moduleSource;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
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