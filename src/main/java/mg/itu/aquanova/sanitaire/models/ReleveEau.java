package mg.itu.aquanova.sanitaire.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.security.models.UserModels;



@Entity
@Table(name = "releve_eau")
public class ReleveEau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bassin", nullable = false)
    private Bassin bassin;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserModels user;

    private Double temperature;

    private Double ph;

    private Double oxygene;

    @Column(name = "date_releve")
    private LocalDate dateReleve;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public ReleveEau() {
        this.bassin = new Bassin();
    }

    public Long getId() {
        return id;
    }

    public Bassin getBassin() {
        return bassin;
    }

    public void setBassin(Bassin bassin) {
        this.bassin = bassin;
    }

    public UserModels getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserModels user) {
        this.user = user;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getOxygene() {
        return oxygene;
    }

    public void setOxygene(Double oxygene) {
        this.oxygene = oxygene;
    }

    public LocalDate getDateReleve() {
        return dateReleve;
    }

    public void setDateReleve(LocalDate dateReleve) {
        this.dateReleve = dateReleve;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}