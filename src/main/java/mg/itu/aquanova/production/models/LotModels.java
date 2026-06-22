package mg.itu.aquanova.production.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lot")
@Getter
@Setter
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

}
