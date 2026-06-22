package mg.itu.aquanova.referentiel.models;

public enum StatutBassin {
    LIBRE(1, "Libre"),
    OCCUPE(2, "Occupé"),
    MAINTENANCE(3, "Maintenance");

    private final int id;
    private final String libelle;

    StatutBassin(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public static StatutBassin fromId(Integer id) {
        if (id == null) {
            return null;
        }

        for (StatutBassin statut : values()) {
            if (statut.id == id) {
                return statut;
            }
        }

        return null;
    }
}
