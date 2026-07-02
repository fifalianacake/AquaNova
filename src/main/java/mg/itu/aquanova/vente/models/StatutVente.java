package mg.itu.aquanova.vente.models;

import jakarta.persistence.*;

@Entity
@Table(name = "statut_vente")
public class StatutVente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StatutVenteEnum code;

    private String libelle;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StatutVenteEnum getCode() { return code; }
    public void setCode(StatutVenteEnum code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}