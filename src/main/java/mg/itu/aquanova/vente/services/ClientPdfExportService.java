package mg.itu.aquanova.vente.services;

import java.awt.Color;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.Vente;

@Service
public class ClientPdfExportService {

    public void export(Client client,
                       List<Vente> ventes,
                       HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=client_" + client.getId() + "_" + client.getNom().replace(" ", "_") + ".pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        Color.BLACK);

        Paragraph p = new Paragraph("FICHE CLIENT", titre);

        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("AquaNova - Pisciculture"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100);

        ajouterLigne(table, "ID", client.getId().toString());
        ajouterLigne(table, "Nom", client.getNom());
        ajouterLigne(table, "Type", client.getTypeClient().getLibelle());
        ajouterLigne(table, "Contact", client.getContact() != null ? client.getContact() : "-");
        ajouterLigne(table, "Email", client.getEmail() != null ? client.getEmail() : "-");
        ajouterLigne(table, "Adresse", client.getAdresse() != null ? client.getAdresse() : "-");
        ajouterLigne(table, "NIF/STAT", client.getNifStat() != null ? client.getNifStat() : "-");
        ajouterLigne(table, "Statut", client.getActif() ? "Actif" : "Inactif");
        ajouterLigne(table, "Observation", client.getObservation() != null ? client.getObservation() : "-");

        document.add(table);

        // Statistiques
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font statsTitre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph statsP = new Paragraph("STATISTIQUES D'ACHAT", statsTitre);
        statsP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(statsP);

        document.add(new Paragraph(" "));

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);

        int nbVentes = ventes.size();
        double totalPoids = ventes.stream().mapToDouble(Vente::getPoidsVendu).sum();
        double totalMontant = ventes.stream().mapToDouble(Vente::getMontantTotal).sum();
        double moyenne = nbVentes > 0 ? totalMontant / nbVentes : 0;
        double prixMoyenKg = totalPoids > 0 ? totalMontant / totalPoids : 0;

        ajouterLigne(statsTable, "Nombre total d'achats", String.valueOf(nbVentes));
        ajouterLigne(statsTable, "Poids total acheté", String.format("%.2f kg", totalPoids));
        ajouterLigne(statsTable, "Chiffre d'affaires total", String.format("%.2f MGA", totalMontant));
        ajouterLigne(statsTable, "Panier moyen", String.format("%.2f MGA", moyenne));
        ajouterLigne(statsTable, "Prix moyen au kg", String.format("%.2f MGA/kg", prixMoyenKg));

        document.add(statsTable);

        // Historique des ventes
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font histoTitre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph histoP = new Paragraph("HISTORIQUE DES ACHATS", histoTitre);
        histoP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(histoP);

        document.add(new Paragraph(" "));

        if (ventes.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucun achat enregistré pour ce client.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable histoTable = new PdfPTable(6);
            histoTable.setWidthPercentage(100);

            // En-têtes
            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Color.WHITE);

            PdfPCell h1 = new PdfPCell(new Phrase("N°", headerFont));
            h1.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Date", headerFont));
            h2.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Poids (kg)", headerFont));
            h3.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("Prix Unitaire", headerFont));
            h4.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h4);

            PdfPCell h5 = new PdfPCell(new Phrase("Montant Total", headerFont));
            h5.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h5);

            PdfPCell h6 = new PdfPCell(new Phrase("Statut", headerFont));
            h6.setBackgroundColor(new Color(0, 102, 204));
            histoTable.addCell(h6);

            // Donnes
            Font cellFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10);

            for (Vente vente : ventes) {

                histoTable.addCell(new PdfPCell(new Phrase(vente.getId().toString(), cellFont)));
                histoTable.addCell(new PdfPCell(new Phrase(vente.getDateVente().toString(), cellFont)));
                histoTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getPoidsVendu()), cellFont)));
                histoTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getPrixUnitaire()), cellFont)));
                histoTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getMontantTotal()), cellFont)));
                histoTable.addCell(new PdfPCell(new Phrase(vente.getStatutVente().getLibelle(), cellFont)));
            }

            document.add(histoTable);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footer =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        Color.GRAY);

        Paragraph footerP = new Paragraph("Document généré par AquaNova", footer);
        footerP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(footerP);

        document.close();

    }

    private void ajouterLigne(
            PdfPTable table,
            String cle,
            String valeur) {

        PdfPCell c1 = new PdfPCell(new Phrase(cle));
        c1.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(valeur));
        table.addCell(c2);
    }
}