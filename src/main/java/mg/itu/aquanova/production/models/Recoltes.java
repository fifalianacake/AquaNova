package mg.itu.aquanova.production.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "recoltes")
public class Recoltes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_Lot", nullable = false)
    private LotModels lot; 

    @ManyToOne
    @JoinColumn(name = "id_type_recolte", nullable = false)
    private TypeRecoltes typeRecolte; 

    @Column(name = "date_recolte", nullable = false)
    private LocalDate dateRecolte;

    @Column(name = "effectif_recolte", nullable = false)
    private int effectifRecolte;

    @Column(name = "poids_total", nullable = false)
    private Double poidsTotal;

    @Column(name = "poids_moyen", nullable = false)
    private Double poidsMoyen;

    public Recoltes() {
    }
    public Recoltes(Long id, LotModels lot, TypeRecoltes typeRecolte, LocalDate dateRecolte, int effectifRecolte,
            Double poidsTotal, Double poidsMoyen) {
        this.id = id;
        this.lot = lot;
        this.typeRecolte = typeRecolte;
        this.dateRecolte = dateRecolte;
        this.effectifRecolte = effectifRecolte;
        this.poidsTotal = poidsTotal;
        this.poidsMoyen = poidsMoyen;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public void setLot(LotModels lot) {
        this.lot = lot;
    }
    public void setTypeRecolte(TypeRecoltes typeRecolte) {
        this.typeRecolte = typeRecolte;
    }
    public void setDateRecolte(LocalDate dateRecolte) {
        this.dateRecolte = dateRecolte;
    }
    public void setEffectifRecolte(int effectifRecolte) {
        this.effectifRecolte = effectifRecolte;
    }
    public void setPoidsTotal(Double poidsTotal) {
        this.poidsTotal = poidsTotal;
    }
    public void setPoidsMoyen(Double poidsMoyen) {
        this.poidsMoyen = poidsMoyen;
    }

    public Long getId() {
        return id;
    }
    public LotModels getLot() {
        return lot;
    }
    public TypeRecoltes getTypeRecolte() {
        return typeRecolte;
    }
    public LocalDate getDateRecolte() {
        return dateRecolte;
    }
    public int getEffectifRecolte() {
        return effectifRecolte;
    }
    public Double getPoidsTotal() {
        return poidsTotal;
    }
    public Double getPoidsMoyen() {
        return poidsMoyen;
    }
}