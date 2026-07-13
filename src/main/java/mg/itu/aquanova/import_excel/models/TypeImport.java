package mg.itu.aquanova.import_excel.models;

import java.util.List;

public enum TypeImport {

    PESEE("Pesées", "pesees",
            List.of("Code du lot", "Date", "Nombre d'échantillons", "Poids total échantillon (g)", "Observation"),
            List.of("LOT-20260210-001", "15/07/2026", "20", "7400", "Pesée mensuelle")),

    MORTALITE("Mortalités", "mortalites",
            List.of("Code du lot", "Date", "Nombre de morts", "Cause"),
            List.of("LOT-20260210-001", "15/07/2026", "12", "Choc thermique")),

    DISTRIBUTION("Distributions d'aliment", "distributions",
            List.of("Code du lot", "Date", "Aliment", "Quantité (kg)"),
            List.of("LOT-20260210-001", "15/07/2026", "Provende croissance", "12.5"));

    private final String libelle;
    private final String slug;
    private final List<String> colonnes;
    private final List<String> exemple;

    TypeImport(String libelle, String slug, List<String> colonnes, List<String> exemple) {
        this.libelle = libelle;
        this.slug = slug;
        this.colonnes = colonnes;
        this.exemple = exemple;
    }

    public static TypeImport parSlug(String slug) {
        for (TypeImport type : values()) {
            if (type.slug.equals(slug)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'import inconnu : " + slug);
    }

    public String getLibelle() {
        return libelle;
    }

    public String getSlug() {
        return slug;
    }

    public List<String> getColonnes() {
        return colonnes;
    }

    public List<String> getExemple() {
        return exemple;
    }

    /** Colonne (0-based) contenant le code du lot : elle reçoit une liste déroulante dans le modèle. */
    public int getIndexColonneLot() {
        return 0;
    }

    /** Colonne (0-based) contenant le nom de l'aliment, ou -1 si le type n'en a pas. */
    public int getIndexColonneAliment() {
        return this == DISTRIBUTION ? 2 : -1;
    }
}
