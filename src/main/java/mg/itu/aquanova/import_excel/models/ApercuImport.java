package mg.itu.aquanova.import_excel.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApercuImport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TypeImport type;
    private final String nomFichier;
    private final List<LigneImport> lignesValides = new ArrayList<>();
    private final List<LigneImport> lignesRejetees = new ArrayList<>();

    public ApercuImport(TypeImport type, String nomFichier) {
        this.type = type;
        this.nomFichier = nomFichier;
    }

    public void ajouter(LigneImport ligne) {
        if (ligne.estValide()) {
            lignesValides.add(ligne);
        } else {
            lignesRejetees.add(ligne);
        }
    }

    public TypeImport getType() {
        return type;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public List<LigneImport> getLignesValides() {
        return lignesValides;
    }

    public List<LigneImport> getLignesRejetees() {
        return lignesRejetees;
    }

    public int getNbLignes() {
        return lignesValides.size() + lignesRejetees.size();
    }

    /**
     * L'import est tout ou rien : une seule ligne rejetée bloque le fichier entier,
     * pour qu'un fichier partiellement importé ne laisse jamais l'utilisateur deviner
     * ce qui est déjà en base et ce qu'il lui reste à ressaisir.
     */
    public boolean isImportPossible() {
        return !lignesValides.isEmpty() && lignesRejetees.isEmpty();
    }
}
