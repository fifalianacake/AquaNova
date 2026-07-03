package mg.itu.aquanova.export_pdf.services;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.models.ListePdfData;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service PDF mutualisé pour tout AquaNova.
 *
 * Objectif : chaque module (achats, ventes, sanitaire, production...) a besoin
 * d'exporter des "fiches" (bon d'achat, facture, fiche fournisseur...) et des
 * "listes filtrées" (historique, rapport...). Plutôt que de réimplémenter la
 * génération PDF dans chaque service, tous les modules passent par les deux
 * méthodes publiques ci-dessous et par les DTOs {@link FichePdfData} /
 * {@link ListePdfData}.
 *
 * Ce service ne va JAMAIS chercher de données lui-même : il reçoit des chaînes
 * déjà formatées. C'est le contrôleur/service métier qui reste responsable des
 * calculs et des filtres, exactement comme les pages web — voir la règle métier
 * "les exports PDF doivent utiliser les mêmes filtres et calculs que les pages web".
 *
 * Dépendance requise dans le pom.xml (voir le fichier fourni à côté de ce service) :
 *
 * <dependency>
 *     <groupId>com.github.librepdf</groupId>
 *     <artifactId>openpdf</artifactId>
 *     <version>3.0.5</version>
 * </dependency>
 */
@Service
public class PdfExportService {

    private static final String NOM_ENTREPRISE = "AquaNova";
    private static final DateTimeFormatter FORMAT_DATE_EXPORT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color COULEUR_ENTETE_TABLE = new Color(21, 101, 129);

    // CP1252 (Windows-1252) couvre les caractères accentués français (é, è, à, ç, ô...)
    // sans avoir besoin d'embarquer une police externe.
    private static final Font FONT_ENTREPRISE =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 18, Font.BOLD, new Color(40, 40, 40));
    private static final Font FONT_TITRE =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 13, Font.BOLD, Color.BLACK);
    private static final Font FONT_SOUS_TITRE =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 10, Font.ITALIC, Color.GRAY);
    private static final Font FONT_SECTION =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 11, Font.BOLD, Color.BLACK);
    private static final Font FONT_LABEL =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 9, Font.BOLD, Color.BLACK);
    private static final Font FONT_VALEUR =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 9, Font.NORMAL, Color.BLACK);
    private static final Font FONT_ENTETE_COLONNE =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 9, Font.BOLD, Color.WHITE);
    private static final Font FONT_CELLULE =
            FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, 9, Font.NORMAL, Color.BLACK);

    /**
     * Génère un PDF "fiche" : bon d'achat, facture de vente, fiche fournisseur,
     * fiche client, fiche équipement, etc. Format portrait A4.
     */
    public byte[] genererFiche(FichePdfData data) {
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, sortie);
            document.open();

            ajouterEntete(document, data.getTitre(), data.getSousTitre());

            for (FichePdfData.Section section : data.getSections()) {
                ajouterSection(document, section);
            }

            if (data.getColonnesTable() != null && !data.getColonnesTable().isEmpty()) {
                if (data.getTitreTable() != null) {
                    Paragraph titreTable = new Paragraph(data.getTitreTable(), FONT_SECTION);
                    titreTable.setSpacingBefore(14);
                    titreTable.setSpacingAfter(4);
                    document.add(titreTable);
                }
                document.add(construireTable(data.getColonnesTable(), data.getLignesTable()));
            }

            if (!data.getTotaux().isEmpty()) {
                document.add(Chunk.NEWLINE);
                for (Map.Entry<String, String> t : data.getTotaux().entrySet()) {
                    Paragraph p = new Paragraph(t.getKey() + " : " + t.getValue(), FONT_SECTION);
                    p.setAlignment(Element.ALIGN_RIGHT);
                    document.add(p);
                }
            }

            document.close();
            return sortie.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du PDF (fiche)", e);
        }
    }

    /**
     * Génère un PDF "liste filtrée" : historique, export de liste, rapport.
     * Format paysage A4 pour laisser de la place à un plus grand nombre de colonnes.
     */
    public byte[] genererListe(ListePdfData data) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 50, 36);
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, sortie);
            document.open();

            ajouterEntete(document, data.getTitre(), null);

            if (!data.getFiltres().isEmpty()) {
                Paragraph titreFiltres = new Paragraph("Filtres appliqués", FONT_SECTION);
                titreFiltres.setSpacingBefore(6);
                document.add(titreFiltres);

                StringBuilder texteFiltres = new StringBuilder();
                for (Map.Entry<String, String> f : data.getFiltres().entrySet()) {
                    if (texteFiltres.length() > 0) {
                        texteFiltres.append("    |    ");
                    }
                    texteFiltres.append(f.getKey()).append(" : ").append(f.getValue());
                }
                Paragraph filtresTexte = new Paragraph(texteFiltres.toString(), FONT_VALEUR);
                filtresTexte.setSpacingAfter(10);
                document.add(filtresTexte);
            }

            document.add(construireTable(data.getColonnes(), data.getLignes()));

            int nbLignes = data.getLignes() != null ? data.getLignes().size() : 0;
            Paragraph pied = new Paragraph(nbLignes + " ligne(s)", FONT_SOUS_TITRE);
            pied.setSpacingBefore(6);
            document.add(pied);

            if (data.getTotalLabel() != null) {
                Paragraph total = new Paragraph(data.getTotalLabel() + " : " + data.getTotalValeur(), FONT_SECTION);
                total.setAlignment(Element.ALIGN_RIGHT);
                total.setSpacingBefore(4);
                document.add(total);
            }

            document.close();
            return sortie.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Erreur lors de la génération du PDF (liste)", e);
        }
    }

    private void ajouterEntete(Document document, String titre, String sousTitre) throws DocumentException {
        document.add(new Paragraph(NOM_ENTREPRISE, FONT_ENTREPRISE));

        Paragraph titreDoc = new Paragraph(titre != null ? titre : "", FONT_TITRE);
        titreDoc.setSpacingBefore(2);
        document.add(titreDoc);

        if (sousTitre != null && !sousTitre.isBlank()) {
            document.add(new Paragraph(sousTitre, FONT_SOUS_TITRE));
        }

        Paragraph dateExport = new Paragraph("Exporté le " + LocalDateTime.now().format(FORMAT_DATE_EXPORT), FONT_SOUS_TITRE);
        dateExport.setSpacingAfter(12);
        document.add(dateExport);
    }

    private void ajouterSection(Document document, FichePdfData.Section section) throws DocumentException {
        Paragraph titre = new Paragraph(section.getTitre(), FONT_SECTION);
        titre.setSpacingBefore(12);
        titre.setSpacingAfter(4);
        document.add(titre);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{35, 65});

        for (Map.Entry<String, String> champ : section.getChamps().entrySet()) {
            PdfPCell labelCell = new PdfPCell(new Phrase(champ.getKey(), FONT_LABEL));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setPadding(4);

            PdfPCell valeurCell = new PdfPCell(new Phrase(valeurOuTiret(champ.getValue()), FONT_VALEUR));
            valeurCell.setBorder(Rectangle.NO_BORDER);
            valeurCell.setPadding(4);

            table.addCell(labelCell);
            table.addCell(valeurCell);
        }
        document.add(table);
    }

    private PdfPTable construireTable(List<String> colonnes, List<List<String>> lignes) throws DocumentException {
        PdfPTable table = new PdfPTable(colonnes.size());
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(5);

        for (String colonne : colonnes) {
            PdfPCell cell = new PdfPCell(new Phrase(colonne, FONT_ENTETE_COLONNE));
            cell.setBackgroundColor(COULEUR_ENTETE_TABLE);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        if (lignes == null || lignes.isEmpty()) {
            PdfPCell vide = new PdfPCell(new Phrase("Aucune donnée pour ces filtres", FONT_CELLULE));
            vide.setColspan(colonnes.size());
            vide.setPadding(10);
            vide.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(vide);
        } else {
            for (List<String> ligne : lignes) {
                for (String valeur : ligne) {
                    PdfPCell cell = new PdfPCell(new Phrase(valeurOuTiret(valeur), FONT_CELLULE));
                    cell.setPadding(4);
                    table.addCell(cell);
                }
            }
        }
        return table;
    }

    private String valeurOuTiret(String valeur) {
        return (valeur != null && !valeur.isBlank()) ? valeur : "-";
    }
}
