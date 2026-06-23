package mg.itu.aquanova.production.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pesee")
public class Pese {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_lot", nullable = false)
    private Long idLot;

    @Column(name = "date_pesee", nullable = false)
    private LocalDate datePesee;

    @Column(name = "nb_echantillon", nullable = false)
    private Integer nbEchantillon;

    @Column(name = "poids_total_echantillon", nullable = false, precision = 10, scale = 2)
    private BigDecimal poidsTotalEchantillon;

    @Column(name = "poids_moyen", nullable = false, precision = 10, scale = 3)
    private BigDecimal poidsMoyen; // Ce champ sera calculé automatiquement

    @Column(columnDefinition = "TEXT")
    private String observation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLot() {
        return idLot;
    }

    public void setIdLot(Long idLot) {
        this.idLot = idLot;
    }

    public LocalDate getDatePesee() {
        return datePesee;
    }

    public void setDatePesee(LocalDate datePesee) {
        this.datePesee = datePesee;
    }

    public Integer getNbEchantillon() {
        return nbEchantillon;
    }

    public void setNbEchantillon(Integer nbEchantillon) {
        this.nbEchantillon = nbEchantillon;
    }

    public BigDecimal getPoidsTotalEchantillon() {
        return poidsTotalEchantillon;
    }

    public void setPoidsTotalEchantillon(BigDecimal poidsTotalEchantillon) {
        this.poidsTotalEchantillon = poidsTotalEchantillon;
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
