package mg.itu.aquanova.alerte.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.models.TypeAlerte;

/**
 * DTO de filtrage pour l'historique des alertes.
 * Tous les champs sont optionnels : un champ null signifie « pas de filtre ».
 */
public class AlerteFilterDTO {

    private ModuleSource moduleSource;
    private TypeAlerte typeAlerte;
    private NiveauCriticite niveauCriticite;
    private StatutAlerte statut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private Long lotId;
    private Long bassinId;

    public AlerteFilterDTO() {
    }

    // ── Getters & Setters ──

    public ModuleSource getModuleSource() {
        return moduleSource;
    }

    public void setModuleSource(ModuleSource moduleSource) {
        this.moduleSource = moduleSource;
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public NiveauCriticite getNiveauCriticite() {
        return niveauCriticite;
    }

    public void setNiveauCriticite(NiveauCriticite niveauCriticite) {
        this.niveauCriticite = niveauCriticite;
    }

    public StatutAlerte getStatut() {
        return statut;
    }

    public void setStatut(StatutAlerte statut) {
        this.statut = statut;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Long getBassinId() {
        return bassinId;
    }

    public void setBassinId(Long bassinId) {
        this.bassinId = bassinId;
    }
}
