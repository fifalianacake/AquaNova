package mg.itu.aquanova.production.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
// StatutLotModels is in the same package; no import needed

@Entity
@Table(name = "lot")

public class LotModels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @ManyToOne
    @JoinColumn(name = "id_espece")
    private EspecesModels espece;

    @ManyToOne
    @JoinColumn(name = "id_bassin")
    private Bassin bassin;

    @ManyToOne
    @JoinColumn(name = "id_stade_croissance")
    private StadeCroissanceModels stadeCroissance;

    @ManyToOne
    @JoinColumn(name = "id_statut_lot")
    private StatutLotModels statutLot;

    @Column(name = "date_mise_en_charge")
    private LocalDate dateMiseEnCharge;

    @Column(name = "effectif_initial")
    private Integer effectifInitial;

    @Column(name = "effectif_actuel")
    private Integer effectifActuel;

    @Column(name = "poids_moyen_initial")
    private Double poidsMoyenInitial;

    @Column(name = "poids_moyen_actuel")
    private Double poidsMoyenActuel;

    private String observation;


    public LotModels() {

    }

    // getters and setters
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public EspecesModels getEspece() {
        return espece;
    }

    public void setEspece(EspecesModels espece) {
        this.espece = espece;
    }

    public Bassin getBassin() {
        return bassin;
    }

    public void setBassin(Bassin bassin) {
        this.bassin = bassin;
    }

    public StadeCroissanceModels getStadeCroissance() {
        return stadeCroissance;
    }

    public void setStadeCroissance(StadeCroissanceModels stadeCroissance) {
        this.stadeCroissance = stadeCroissance;
    }

    public StatutLotModels getStatutLot() {
        return statutLot;
    }

    public void setStatutLot(StatutLotModels statutLot) {
        this.statutLot = statutLot;
    }


    public LocalDate getDateMiseEnCharge() {
        return dateMiseEnCharge;
    }


    public void setDateMiseEnCharge(LocalDate dateMiseEnCharge) {
        this.dateMiseEnCharge = dateMiseEnCharge;
    }


    public Integer getEffectifInitial() {
        return effectifInitial;
    }


    public void setEffectifInitial(Integer effectifInitial) {
        this.effectifInitial = effectifInitial;
    }


    public Integer getEffectifActuel() {
        return effectifActuel;
    }


    public void setEffectifActuel(Integer effectifActuel) {
        this.effectifActuel = effectifActuel;
    }


    public Double getPoidsMoyenInitial() {
        return poidsMoyenInitial;
    }


    public void setPoidsMoyenInitial(Double poidsMoyenInitial) {
        this.poidsMoyenInitial = poidsMoyenInitial;
    }


    public Double getPoidsMoyenActuel() {
        return poidsMoyenActuel;
    }


    public void setPoidsMoyenActuel(Double poidsMoyenActuel) {
        this.poidsMoyenActuel = poidsMoyenActuel;
    }


    public String getObservation() {
        return observation;
    }


    public void setObservation(String observation) {
        this.observation = observation;
    }

    

}
