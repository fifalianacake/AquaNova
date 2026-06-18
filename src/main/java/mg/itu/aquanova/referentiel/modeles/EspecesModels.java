package mg.itu.aquanova.referentiel.modeles;

import java.math.BigDecimal;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "especes")
public class EspecesModels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "poids_cible_moyen", precision = 6, scale = 2)
    private BigDecimal poidsCibleMoyen;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    public EspecesModels() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPoidsCibleMoyen() {
        return poidsCibleMoyen;
    }

    public void setPoidsCibleMoyen(BigDecimal poidsCibleMoyen) {
        this.poidsCibleMoyen = poidsCibleMoyen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
