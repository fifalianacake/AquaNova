package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "statut_bassin")
public class StatutBassin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "libelle", nullable = false, unique = true, length = 20)
    private LibelleStatutBassin libelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LibelleStatutBassin getLibelle() {
        return libelle;
    }

    public void setLibelle(LibelleStatutBassin libelle) {
        this.libelle = libelle;
    }

    
}
