package mg.itu.aquanova.alerte.dto;

import mg.itu.aquanova.alerte.models.StatutAlerte;

public class UpdateStatutAlerteDTO {

    private StatutAlerte nouveauStatut;
    private String commentaire;

    public UpdateStatutAlerteDTO() {
    }

    public UpdateStatutAlerteDTO(StatutAlerte nouveauStatut, String commentaire) {
        this.nouveauStatut = nouveauStatut;
        this.commentaire = commentaire;
    }

    public StatutAlerte getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(StatutAlerte nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
