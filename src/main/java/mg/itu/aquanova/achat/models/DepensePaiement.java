package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "depense_paiement")
public class DepensePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_depense", nullable = false)
    private Depense depense;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 40)
    private ModePaiementEnum modePaiement;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal montant = BigDecimal.ZERO;

    @Column(length = 100)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public DepensePaiement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Depense getDepense() {
        return depense;
    }

    public void setDepense(Depense depense) {
        this.depense = depense;
    }

    public ModePaiementEnum getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiementEnum modePaiement) {
        this.modePaiement = modePaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
