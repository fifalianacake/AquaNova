package mg.itu.aquanova.achat.models;

import java.math.BigDecimal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.EspecesModels;

@Entity
@Table(name = "ligne_achat")
public class LigneAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_achat", nullable = false)
    private Achat achat;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantite;

    @Column(nullable = false, length = 30)
    private String unite;

    @Column(name = "prix_unitaire", nullable = false, precision = 14, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ligne", nullable = false, precision = 14, scale = 2)
    private BigDecimal montantLigne;

    @ManyToOne
    @JoinColumn(name = "id_aliment")
    private Aliment aliment;

    @ManyToOne
    @JoinColumn(name = "id_intrant")
    private Intrant intrant;

    @ManyToOne
    @JoinColumn(name = "id_espece")
    private EspecesModels espece;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_lot")
    private LotModels lot;

    /**
     * Bassin cible d'un achat d'alevins, choisi dès le brouillon mais utilisé
     * seulement à la validation pour créer le lot et occuper le bassin.
     */
    @ManyToOne
    @JoinColumn(name = "id_bassin")
    private Bassin bassin;

    /**
     * Poids moyen (g) saisi pour un achat d'alevins, conservé jusqu'à la validation
     * afin d'en déduire le stade de croissance du lot créé.
     */
    @Column(name = "poids_moyen", precision = 10, scale = 2)
    private BigDecimal poidsMoyen;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public LigneAchat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Achat getAchat() {
        return achat;
    }

    public void setAchat(Achat achat) {
        this.achat = achat;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }

    public Aliment getAliment() {
        return aliment;
    }

    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public Intrant getIntrant() {
        return intrant;
    }

    public void setIntrant(Intrant intrant) {
        this.intrant = intrant;
    }

    public EspecesModels getEspece() {
        return espece;
    }

    public void setEspece(EspecesModels espece) {
        this.espece = espece;
    }

    public LotModels getLot() {
        return lot;
    }

    public void setLot(LotModels lot) {
        this.lot = lot;
    }

    public Bassin getBassin() {
        return bassin;
    }

    public void setBassin(Bassin bassin) {
        this.bassin = bassin;
    }

    public BigDecimal getPoidsMoyen() {
        return poidsMoyen;
    }

    public void setPoidsMoyen(BigDecimal poidsMoyen) {
        this.poidsMoyen = poidsMoyen;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
