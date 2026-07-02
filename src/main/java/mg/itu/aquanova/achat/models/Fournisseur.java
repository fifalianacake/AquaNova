package mg.itu.aquanova.achat.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fournisseur")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_fournisseur", nullable = false, length = 30)
    private TypeFournisseur typeFournisseur;

    @Column(length = 100)
    private String contact;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "nif_stat", length = 100)
    private String nifStat;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public Fournisseur() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeFournisseur getTypeFournisseur() {
        return typeFournisseur;
    }

    public void setTypeFournisseur(TypeFournisseur typeFournisseur) {
        this.typeFournisseur = typeFournisseur;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNifStat() {
        return nifStat;
    }

    public void setNifStat(String nifStat) {
        this.nifStat = nifStat;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
