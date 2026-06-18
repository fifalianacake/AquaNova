package mg.itu.aquanova.referentiel.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


@Entity
@Table(name = "bassin")
@Data
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
}
