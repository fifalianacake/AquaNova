package mg.itu.aquanova.sanitaire_equipement.models;

import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.security.models.UserModels;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "traitement_eau")
public class TraitementEau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bassin", nullable = false)
    private Bassin bassin;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserModels utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_type_traitement_eau", nullable = false)
    private TypeTraitementEau typeTraitementEau;

    @Column(name = "date_traitement", nullable = false)
    private LocalDate dateTraitement;

    @Column(nullable = false)
    private String detail;

    private String observation;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bassin getBassin() { return bassin; }
    public void setBassin(Bassin bassin) { this.bassin = bassin; }

    public UserModels getUtilisateur() { return utilisateur; }
    public void setUtilisateur(UserModels utilisateur) { this.utilisateur = utilisateur; }

    public TypeTraitementEau getTypeTraitementEau() { return typeTraitementEau; }
    public void setTypeTraitementEau(TypeTraitementEau typeTraitementEau) { this.typeTraitementEau = typeTraitementEau; }

    public LocalDate getDateTraitement() { return dateTraitement; }
    public void setDateTraitement(LocalDate dateTraitement) { this.dateTraitement = dateTraitement; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}