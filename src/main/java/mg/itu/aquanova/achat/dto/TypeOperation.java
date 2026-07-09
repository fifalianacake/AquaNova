package mg.itu.aquanova.achat.dto;

public enum TypeOperation {
    ACHAT("Achat"),
    DEPENSE("Dépense");

    private final String libelle;

    TypeOperation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
