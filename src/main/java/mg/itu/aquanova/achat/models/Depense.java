package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "depense")
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_depense", nullable = false)
    private LocalDate dateDepense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie_depense", nullable = false)
    private CategorieDepense categorieDepense;

    @Column(nullable = false, length = 180)
    private String libelle;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal montant = BigDecimal.ZERO;

    @Column(name = "mode_paiement", length = 80)
    private String modePaiement;

    @Column(length = 100)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @OneToMany(mappedBy = "depense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepensePaiement> paiements = new ArrayList<>();

    public Depense() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDepense() {
        return dateDepense;
    }

    public void setDateDepense(LocalDate dateDepense) {
        this.dateDepense = dateDepense;
    }

    public CategorieDepense getCategorieDepense() {
        return categorieDepense;
    }

    public void setCategorieDepense(CategorieDepense categorieDepense) {
        this.categorieDepense = categorieDepense;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
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

    public List<DepensePaiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<DepensePaiement> paiements) {
        this.paiements = paiements == null ? new ArrayList<>() : paiements;
        this.paiements.forEach(p -> p.setDepense(this));
    }

    public void addPaiement(DepensePaiement paiement) {
        if (paiement != null) {
            paiement.setDepense(this);
            this.paiements.add(paiement);
        }
    }

    public BigDecimal calculerTotalPaiements() {
        return paiements.stream()
                .map(paiement -> paiement.getMontant())
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public void reglerTotalPaiements() {
        if (paiements != null && !paiements.isEmpty()) {
            this.montant = calculerTotalPaiements();
        }
    }
}
