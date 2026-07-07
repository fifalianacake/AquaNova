package mg.itu.aquanova.achat.models;

public enum ModePaiementEnum {
    ESPECES("Espèces"),
    MOBILE_MONEY("Mobile money"),
    VIREMENT("Virement"),
    CHEQUE("Chèque"),
    AUTRE("Autre");

    private final String libelle;

    ModePaiementEnum(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
