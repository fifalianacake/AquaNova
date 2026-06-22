package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "bassin")
public class Bassin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "id_statut", nullable = false)
    private Integer idStatut;

    @Column(name = "capacite_m3", nullable = false, precision = 10, scale = 2)
    private BigDecimal capaciteM3;

    // Jointure : Plusieurs bassins peuvent avoir le même type
    @ManyToOne
    @JoinColumn(name = "id_type", nullable = false)
    private TypeBassin typeBassin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(Integer idStatut) {
        this.idStatut = idStatut;
    }

    @Transient
    public StatutBassin getStatut() {
        return StatutBassin.fromId(idStatut);
    }

    public void setStatut(StatutBassin statut) {
        this.idStatut = statut != null ? statut.getId() : null;
    }

    public BigDecimal getCapaciteM3() {
        return capaciteM3;
    }

    public void setCapaciteM3(BigDecimal capaciteM3) {
        this.capaciteM3 = capaciteM3;
    }

    public TypeBassin getTypeBassin() {
        return typeBassin;
    }

    public void setTypeBassin(TypeBassin typeBassin) {
        this.typeBassin = typeBassin;
    }
}
