package mg.itu.aquanova.production.models;

import jakarta.persistence.*;

@Entity
@Table(name = "type_traitement_eau") // T2 - Nom exact au singulier
public class TypeTraitementEau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    private String description;

    // Getters et Setters standard (Pas de Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}