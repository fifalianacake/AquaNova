package mg.itu.aquanova.production.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journaux_lots")
public class JournalLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_lot", nullable = false)
    private Lot lot;

    @ManyToOne
    @JoinColumn(name = "id_type_evenement", nullable = false)
    private TypeEvenementLot typeEvenement;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    @Column(nullable = false)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public TypeEvenementLot getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(TypeEvenementLot typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}