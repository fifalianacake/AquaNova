package mg.itu.aquanova.alerte.models;

/**
 * Type fonctionnel de l'alerte.
 */
public enum TypeAlerte {
    /** Stock sous le seuil minimal, mais pas encore épuisé. */
    STOCK_BAS,
    /** Stock épuisé, ou rupture prévue sous JOURS_AVANT_RUPTURE_STOCK jours. */
    RUPTURE_STOCK,
    MORTALITE_ELEVEE,
    QUALITE_EAU,
    MAINTENANCE,
    RECOLTE_PROCHE,
    PEREMPTION,
    SEUIL_DEPASSE,
    AUTRE
}
