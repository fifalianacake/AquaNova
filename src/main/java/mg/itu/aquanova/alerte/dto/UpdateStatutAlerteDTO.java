package mg.itu.aquanova.alerte.dto;

public class UpdateStatutAlerteDTO {

    private Long idStatut;
    private String commentaire;

    public UpdateStatutAlerteDTO() {
    }

    public Long getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(Long idStatut) {
        this.idStatut = idStatut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}