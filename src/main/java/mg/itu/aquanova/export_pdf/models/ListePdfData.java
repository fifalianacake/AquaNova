package mg.itu.aquanova.export_pdf.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Données prêtes à imprimer pour un PDF de type "liste filtrée" : export d'une page
 * de liste, d'un historique ou d'un rapport (dépenses, ventes, mouvements de stock...).
 *
 * Le principe : le contrôleur appelle exactement le même service et les mêmes filtres
 * que pour la page web, formate les résultats en texte, et les donne à ce DTO.
 * Le PDF reste ainsi toujours cohérent avec ce que l'utilisateur voit à l'écran.
 *
 * Usage typique :
 *
 * ListePdfData data = ListePdfData.of("Historique des dépenses")
 *     .filtre("Catégorie", filtre.getCategorieLibelle())
 *     .filtre("Du", filtre.getDateDebut())
 *     .filtre("Au", filtre.getDateFin())
 *     .colonnes(List.of("Date", "Catégorie", "Libellé", "Montant", "Mode paiement"))
 *     .lignes(lignesFormatees)
 *     .total("Total", montantTotal + " Ar");
 */
public class ListePdfData {

    private String titre;
    private final Map<String, String> filtres = new LinkedHashMap<>();
    private List<String> colonnes;
    private List<List<String>> lignes;
    private String totalLabel;
    private String totalValeur;

    private ListePdfData() {
    }

    public static ListePdfData of(String titre) {
        ListePdfData data = new ListePdfData();
        data.titre = titre;
        return data;
    }

    /**
     * N'ajoute le filtre à l'affichage que s'il a réellement été renseigné par
     * l'utilisateur (valeur non nulle et non vide) : seuls les filtres actifs
     * apparaissent sur le PDF, exactement comme sur la page de liste.
     */
    public ListePdfData filtre(String label, Object valeur) {
        if (valeur != null && !valeur.toString().isBlank()) {
            this.filtres.put(label, valeur.toString());
        }
        return this;
    }

    public ListePdfData colonnes(List<String> colonnes) {
        this.colonnes = colonnes;
        return this;
    }

    public ListePdfData lignes(List<List<String>> lignes) {
        this.lignes = lignes;
        return this;
    }

    public ListePdfData total(String label, String valeur) {
        this.totalLabel = label;
        this.totalValeur = valeur;
        return this;
    }

    public String getTitre() {
        return titre;
    }

    public Map<String, String> getFiltres() {
        return filtres;
    }

    public List<String> getColonnes() {
        return colonnes;
    }

    public List<List<String>> getLignes() {
        return lignes;
    }

    public String getTotalLabel() {
        return totalLabel;
    }

    public String getTotalValeur() {
        return totalValeur;
    }
}
