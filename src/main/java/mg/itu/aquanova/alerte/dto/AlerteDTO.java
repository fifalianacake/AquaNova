package mg.itu.aquanova.alerte.dto;

import java.time.LocalDateTime;

public class AlerteDTO {

    private Long id;
    private String moduleSource;
    private String typeAlerteCode;
    private String typeAlerte;
    private String niveauCriticiteCode;
    private String niveauCriticite;
    private Integer niveauOrdre; // pour le tri et la couleur
    private String statutAlerteCode;
    private String statutAlerte;
    private String message;
    private String entiteType;
    private Long entiteId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private String commentaireResolution;

    public AlerteDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModuleSource() { return moduleSource; }
    public void setModuleSource(String moduleSource) { this.moduleSource = moduleSource; }

    public String getTypeAlerteCode() { return typeAlerteCode; }
    public void setTypeAlerteCode(String typeAlerteCode) { this.typeAlerteCode = typeAlerteCode; }

    public String getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(String typeAlerte) { this.typeAlerte = typeAlerte; }

    public String getNiveauCriticiteCode() { return niveauCriticiteCode; }
    public void setNiveauCriticiteCode(String niveauCriticiteCode) { this.niveauCriticiteCode = niveauCriticiteCode; }

    public String getNiveauCriticite() { return niveauCriticite; }
    public void setNiveauCriticite(String niveauCriticite) { this.niveauCriticite = niveauCriticite; }

    public Integer getNiveauOrdre() { return niveauOrdre; }
    public void setNiveauOrdre(Integer niveauOrdre) { this.niveauOrdre = niveauOrdre; }

    public String getStatutAlerteCode() { return statutAlerteCode; }
    public void setStatutAlerteCode(String statutAlerteCode) { this.statutAlerteCode = statutAlerteCode; }

    public String getStatutAlerte() { return statutAlerte; }
    public void setStatutAlerte(String statutAlerte) { this.statutAlerte = statutAlerte; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEntiteType() { return entiteType; }
    public void setEntiteType(String entiteType) { this.entiteType = entiteType; }

    public Long getEntiteId() { return entiteId; }
    public void setEntiteId(Long entiteId) { this.entiteId = entiteId; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }

    public String getCommentaireResolution() { return commentaireResolution; }
    public void setCommentaireResolution(String c) { this.commentaireResolution = c; }
}
