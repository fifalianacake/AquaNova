package mg.itu.aquanova.production.models;

import mg.itu.aquanova.referentiel.models.Bassin;
import  mg.itu.aquanova.security.models.UserModels;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traitement_eau") // T3 - Nom exact au singulier
public class TraitementEau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bassin", nullable = false) // FK → bassin.id
    private Bassin bassin;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false) // FK → utilisateur.id
    private UserModels utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_type_traitement_eau", nullable = false) // FK → type_traitement_eau.id
    private TypeTraitementEau typeTraitementEau;

    @Column(name = "date_traitement", nullable = false)
    private LocalDate dateTraitement;

    @Column(nullable = false)
    private String detail;

    private String observation;

    // Getters et Setters standard (Pas de Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bassin getBassin() { return bassin; }
    public void setBassin(Bassin bassin) { this.bassin = bassin; }

    public UserModels getUtilisateur() { return utilisateur; }
    public void setUtilisateur(UserModels utilisateur) { this.utilisateur = utilisateur; }

    public TypeTraitementEau getTypeTraitementEau() { return typeTraitementEau; }
    public void setTypeTraitementEau(TypeTraitementEau typeTraitementEau) { this.typeTraitementEau = typeTraitementEau; }

    public LocalDate getDateTraitement() { return dateTraitement; }
    public void setDateTraitement(LocalDate dateTreatment) { this.dateTraitement = dateTreatment; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}