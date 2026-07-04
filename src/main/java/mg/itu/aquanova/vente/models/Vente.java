package mg.itu.aquanova.vente.models;

import jakarta.persistence.*;
import mg.itu.aquanova.production.models.Recoltes; // Modification ici (Recoltes avec un s)
import java.time.LocalDate;

@Entity
@Table(name = "vente")
public class Vente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_recolte", nullable = false)
    private Recoltes recolte; // Modifié en Recoltes

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @Column(name = "date_vente", nullable = false)
    private LocalDate dateVente;

    @Column(name = "poids_vendu", nullable = false)
    private Double poidsVendu;

    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;

    @Column(name = "effectif_vendu")
    private Integer effectifVendu;

    private String observation;

    @ManyToOne
    @JoinColumn(name = "id_statut_vente", nullable = false)
    private StatutVente statutVente;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Recoltes getRecolte() { return recolte; }
    public void setRecolte(Recoltes recolte) { this.recolte = recolte; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDate getDateVente() { return dateVente; }
    public void setDateVente(LocalDate dateVente) { this.dateVente = dateVente; }
    public Double getPoidsVendu() { return poidsVendu; }
    public void setPoidsVendu(Double poidsVendu) { this.poidsVendu = poidsVendu; }
    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public Integer getEffectifVendu() { return effectifVendu; }
    public void setEffectifVendu(Integer effectifVendu) { this.effectifVendu = effectifVendu; }
    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
    public StatutVente getStatutVente() { return statutVente; }
    public void setStatutVente(StatutVente statutVente) { this.statutVente = statutVente; }

    public Double getMontantTotal() {
        return (this.poidsVendu != null && this.prixUnitaire != null) ? this.poidsVendu * this.prixUnitaire : 0.0;
    }
}