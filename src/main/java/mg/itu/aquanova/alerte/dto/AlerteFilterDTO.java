package mg.itu.aquanova.alerte.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class AlerteFilterDTO {

    private String moduleSource;
    private String typeAlerte;
    private String niveauCriticite;
    private String statutAlerte;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDebut;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFin;

    private String motCle;
    private int page = 0;
    private int taille = 10;

    public AlerteFilterDTO() {}

    public String getModuleSource() { return moduleSource; }
    public void setModuleSource(String moduleSource) { this.moduleSource = moduleSource; }

    public String getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(String typeAlerte) { this.typeAlerte = typeAlerte; }

    public String getNiveauCriticite() { return niveauCriticite; }
    public void setNiveauCriticite(String niveauCriticite) { this.niveauCriticite = niveauCriticite; }

    public String getStatutAlerte() { return statutAlerte; }
    public void setStatutAlerte(String statutAlerte) { this.statutAlerte = statutAlerte; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getTaille() { return taille; }
    public void setTaille(int taille) { this.taille = taille; }
}