package mg.itu.aquanova.production.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "lot")

public class LotModels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(name = "id_espece")
    private Long idEspece;

    @Column(name = "id_bassin")
    private Long idBassin;

    @Column(name = "id_stade_croissance")
    private Long idStadeCroissance;

    @Column(name = "id_statut_lot")
    private Long idStatutLot;

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


    public Long getIdEspece() {
        return idEspece;
    }


    public void setIdEspece(Long idEspece) {
        this.idEspece = idEspece;
    }


    public Long getIdBassin() {
        return idBassin;
    }


    public void setIdBassin(Long idBassin) {
        this.idBassin = idBassin;
    }


    public Long getIdStadeCroissance() {
        return idStadeCroissance;
    }


    public void setIdStadeCroissance(Long idStadeCroissance) {
        this.idStadeCroissance = idStadeCroissance;
    }


    public Long getIdStatutLot() {
        return idStatutLot;
    }


    public void setIdStatutLot(Long idStatutLot) {
        this.idStatutLot = idStatutLot;
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
