package mg.itu.aquanova.production.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mortalite")
public class MortaliteModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_lot", nullable = false)
    private Integer idLot;

    @Column(name = "date_mortalite", nullable = false)
    private LocalDate dateMortalite;

    @Column(name = "nb_morts", nullable = false)
    private Integer nbMorts;

    @Column(name = "cause")
    private String cause;

    public MortaliteModels() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdLot() {
        return idLot;
    }

    public void setIdLot(Integer idLot) {
        this.idLot = idLot;
    }

    public LocalDate getDateMortalite() {
        return dateMortalite;
    }

    public void setDateMortalite(LocalDate dateMortalite) {
        this.dateMortalite = dateMortalite;
    }

    public Integer getNbMorts() {
        return nbMorts;
    }

    public void setNbMorts(Integer nbMorts) {
        this.nbMorts = nbMorts;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
