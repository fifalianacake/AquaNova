package mg.itu.aquanova.production.models;

import jakarta.persistence.*;
import mg.itu.aquanova.referentiel.models.*;

@Entity
@Table(name = "transfert")
public class TransfertModels {

    // Transfert :
    // id
    // id_lot_source
    // id_lot_destination
    // id_bassin_source
    // id_bassin_destination
    // date_transfert
    // effectif
    // poids_moyen

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot_source", nullable = false)
    private LotModels lotSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lot_destination", nullable = false)
    private LotModels lotDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bassin_source", nullable = false)
    private Bassin bassinSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bassin_destination", nullable = false)
    private Bassin bassinDestination;

    @Column(name = "date_transfert", nullable = false)
    private java.time.LocalDate dateTransfert;

    @Column(name = "effectif", nullable = false)
    private Integer effectif;

    @Column(name = "poids_moyen", nullable = false, precision = 10, scale = 2)
    private Double poidsMoyen;

    @Transient
    private String codeLotDestination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotModels getLotSource() {
        return lotSource;
    }

    public void setLotSource(LotModels lotSource) {
        this.lotSource = lotSource;
    }

    public LotModels getLotDestination() {
        return lotDestination;
    }

    public void setLotDestination(LotModels lotDestination) {
        this.lotDestination = lotDestination;
    }

    public Bassin getBassinSource() {
        return bassinSource;
    }

    public void setBassinSource(Bassin bassinSource) {
        this.bassinSource = bassinSource;
    }

    public Bassin getBassinDestination() {
        return bassinDestination;
    }

    public void setBassinDestination(Bassin bassinDestination) {
        this.bassinDestination = bassinDestination;
    }

    public java.time.LocalDate getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(java.time.LocalDate dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public Integer getEffectif() {
        return effectif;
    }

    public void setEffectif(Integer effectif) {
        this.effectif = effectif;
    }

    public Double getPoidsMoyen() {
        return poidsMoyen;
    }

    public void setPoidsMoyen(Double poidsMoyen) {
        this.poidsMoyen = poidsMoyen;
    }

    public String getCodeLotDestination() {
        return codeLotDestination;
    }

    public void setCodeLotDestination(String codeLotDestination) {
        this.codeLotDestination = codeLotDestination;
    }

}
