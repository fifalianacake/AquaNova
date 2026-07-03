package mg.itu.aquanova.export_pdf.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Données prêtes à imprimer pour un PDF de type "fiche" : un seul enregistrement
 * (bon d'achat, facture de vente, fiche fournisseur, fiche client, fiche équipement...).
 *
 * Une fiche est composée de sections "libellé : valeur" (ex: en-tête de l'achat),
 * éventuellement suivies d'un tableau de lignes (ex: les lignes d'achat) et d'un
 * ou plusieurs totaux.
 *
 * Usage typique dans un service ou un contrôleur :
 *
 * FichePdfData data = FichePdfData.of("Bon d'achat #" + achat.getId())
 *     .sousTitre("Statut : " + achat.getStatutAchat())
 *     .section("Informations générales", Map.of(
 *         "Date d'achat", achat.getDateAchat().toString(),
 *         "Fournisseur", achat.getFournisseur().getNom(),
 *         "Référence facture", achat.getReferenceFacture()
 *     ))
 *     .table("Lignes d'achat",
 *         List.of("Désignation", "Quantité", "Prix unitaire", "Montant"),
 *         lignes)
 *     .total("Montant total", achat.getMontantTotal() + " Ar");
 */
public class FichePdfData {

    private String titre;
    private String sousTitre;
    private final List<Section> sections = new ArrayList<>();
    private String titreTable;
    private List<String> colonnesTable;
    private List<List<String>> lignesTable;
    private final Map<String, String> totaux = new LinkedHashMap<>();

    private FichePdfData() {
    }

    public static FichePdfData of(String titre) {
        FichePdfData data = new FichePdfData();
        data.titre = titre;
        return data;
    }

    public FichePdfData sousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
        return this;
    }

    /**
     * Ajoute un bloc "libellé : valeur". L'ordre d'insertion de la Map est conservé
     * si vous utilisez une LinkedHashMap (recommandé) ou Map.of() pour de petits blocs.
     */
    public FichePdfData section(String titreSection, Map<String, String> champs) {
        this.sections.add(new Section(titreSection, champs));
        return this;
    }

    /**
     * Ajoute un tableau de détail (ex : lignes d'achat, historique de paiements...).
     * Passez des List<String> déjà formatées (dates, montants...) : le service PDF
     * n'effectue aucun calcul, il affiche ce qu'on lui donne.
     */
    public FichePdfData table(String titreTable, List<String> colonnes, List<List<String>> lignes) {
        this.titreTable = titreTable;
        this.colonnesTable = colonnes;
        this.lignesTable = lignes;
        return this;
    }

    public FichePdfData total(String label, String valeur) {
        this.totaux.put(label, valeur);
        return this;
    }

    public String getTitre() {
        return titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getTitreTable() {
        return titreTable;
    }

    public List<String> getColonnesTable() {
        return colonnesTable;
    }

    public List<List<String>> getLignesTable() {
        return lignesTable;
    }

    public Map<String, String> getTotaux() {
        return totaux;
    }

    public static class Section {
        private final String titre;
        private final Map<String, String> champs;

        Section(String titre, Map<String, String> champs) {
            this.titre = titre;
            this.champs = champs;
        }

        public String getTitre() {
            return titre;
        }

        public Map<String, String> getChamps() {
            return champs;
        }
    }
}
