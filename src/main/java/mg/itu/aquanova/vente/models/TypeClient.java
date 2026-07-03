package mg.itu.aquanova.vente.models;

import jakarta.persistence.*;

@Entity
@Table(name = "type_client")
public class TypeClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TypeClientEnum code;

    @Column(nullable = false)
    private String libelle;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TypeClientEnum getCode() { return code; }
    public void setCode(TypeClientEnum code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}