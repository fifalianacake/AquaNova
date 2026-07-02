package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "achat")
public class Achat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;

    @ManyToOne
    @JoinColumn(name = "id_fournisseur", nullable = false)
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "id_categorie_depense", nullable = false)
    private CategorieDepense categorieDepense;

    @Column(name = "reference_facture", length = 100)
    private String referenceFacture;

    @Column(name = "montant_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_achat", nullable = false, length = 30)
    private StatutAchat statutAchat = StatutAchat.BROUILLON;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @OneToMany(mappedBy = "achat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneAchat> lignes = new ArrayList<>();

    public Achat() {
    }

    public void addLigne(LigneAchat ligne) {
        lignes.add(ligne);
        ligne.setAchat(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public CategorieDepense getCategorieDepense() {
        return categorieDepense;
    }

    public void setCategorieDepense(CategorieDepense categorieDepense) {
        this.categorieDepense = categorieDepense;
    }

    public String getReferenceFacture() {
        return referenceFacture;
    }

    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public StatutAchat getStatutAchat() {
        return statutAchat;
    }

    public void setStatutAchat(StatutAchat statutAchat) {
        this.statutAchat = statutAchat;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public List<LigneAchat> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneAchat> lignes) {
        this.lignes = lignes;
    }
}
