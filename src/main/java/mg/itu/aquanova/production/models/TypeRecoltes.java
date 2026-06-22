package mg.itu.aquanova.production.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_recoltes")
public class TypeRecoltes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle", nullable = false, unique = true, length = 100)
    private String libelle;

    @Column(name = "description", nullable = false)
    private String description;

    public TypeRecoltes() {
    }
    public TypeRecoltes(Long id, String libelle, String description) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }
    public String getLibelle() {
        return libelle;
    }
    public String getDescription() {
        return description;
    }
}
