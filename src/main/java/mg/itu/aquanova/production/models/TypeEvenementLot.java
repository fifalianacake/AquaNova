package mg.itu.aquanova.production.models;

import jakarta.persistence.*;

@Entity
@Table(name = "types_evenements_lot")
public class TypeEvenementLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private LibelleEvenement libelle;

    public enum LibelleEvenement {
        TRANSFERT, RECOLTE, PESEE, MORTALITE
    }

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

    public LibelleEvenement getLibelle() {
        return libelle;
    }

    public void setLibelle(LibelleEvenement libelle) {
        this.libelle = libelle;
    }
}