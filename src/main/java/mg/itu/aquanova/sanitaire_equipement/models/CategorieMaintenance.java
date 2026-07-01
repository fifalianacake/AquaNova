package mg.itu.aquanova.sanitaire_equipement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorie_maintenance")
public class CategorieMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategorieMaintenanceEnum libelle;

    @Column(length = 255)
    private String description;

    public CategorieMaintenance(Long id, CategorieMaintenanceEnum libelle, String description) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
    }

    public CategorieMaintenance() {
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public void setLibelle(CategorieMaintenanceEnum libelle) {
        this.libelle = libelle;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }
    public CategorieMaintenanceEnum getLibelle() {
        return libelle;
    }
    public String getDescription() {
        return description;
    }
}
