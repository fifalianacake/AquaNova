package mg.itu.aquanova.import_excel.models;

import java.io.Serializable;
import java.util.List;

public class LigneImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Numéro de la ligne dans le fichier Excel (1-based), pour que l'utilisateur la retrouve. */
    private final int numeroLigne;
    private final List<String> cellules;
    private String erreur;

    public LigneImport(int numeroLigne, List<String> cellules) {
        this.numeroLigne = numeroLigne;
        this.cellules = cellules;
    }

    public String cellule(int index) {
        if (index < 0 || index >= cellules.size()) {
            return null;
        }
        String valeur = cellules.get(index);
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }

    public boolean estValide() {
        return erreur == null;
    }

    public int getNumeroLigne() {
        return numeroLigne;
    }

    public List<String> getCellules() {
        return cellules;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }
}
