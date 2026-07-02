package mg.itu.aquanova.vente.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import mg.itu.aquanova.vente.models.Vente;

@Service
public class HistoriquePdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void exportHistorique(
            List<Vente> ventes,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            String clientNom,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=historique_ventes_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        Color.BLACK);

        Paragraph p = new Paragraph("HISTORIQUE COMMERCIAL DES VENTES", titre);

        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("AquaNova - Pisciculture"));
        document.add(new Paragraph(" "));

        // Filtres
        Font filterFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12,
                        new Color(0, 102, 204));

        Paragraph filters = new Paragraph("Filtres appliqués :", filterFont);
        document.add(filters);

        Font filterValueFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        12);

        if (dateDebut != null) {
            document.add(new Paragraph("Date début : " + dateDebut.format(DATE_FORMATTER), filterValueFont));
        }
        if (dateFin != null) {
            document.add(new Paragraph("Date fin : " + dateFin.format(DATE_FORMATTER), filterValueFont));
        }
        if (clientNom != null && !clientNom.isEmpty()) {
            document.add(new Paragraph("Client : " + clientNom, filterValueFont));
        }

        document.add(new Paragraph(" "));

        // Statistiques
        Font statsTitre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph statsP = new Paragraph("RESUME STATISTIQUE", statsTitre);
        statsP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(statsP);

        document.add(new Paragraph(" "));

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);

        int nbVentes = ventes.size();
        double totalPoids = ventes.stream().mapToDouble(Vente::getPoidsVendu).sum();
        double totalMontant = ventes.stream().mapToDouble(Vente::getMontantTotal).sum();
        double prixMoyenKg = totalPoids > 0 ? totalMontant / totalPoids : 0;

        ajouterLigne(statsTable, "Nombre total de ventes", String.valueOf(nbVentes));
        ajouterLigne(statsTable, "Poids total vendu", String.format("%.2f kg", totalPoids));
        ajouterLigne(statsTable, "Chiffre d'affaires total", String.format("%.2f MGA", totalMontant));
        ajouterLigne(statsTable, "Prix moyen au kg", String.format("%.2f MGA/kg", prixMoyenKg));

        document.add(statsTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Liste des ventes
        Font listTitre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph listP = new Paragraph("LISTE DES VENTES", listTitre);
        listP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(listP);

        document.add(new Paragraph(" "));

        if (ventes.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucune vente trouvée avec ces filtres.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            Color.WHITE);

            String[] headers = {"N°", "Date", "Client", "Lot", "Poids (kg)", "Prix Unitaire", "Montant Total", "Statut"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(0, 102, 204));
                cell.setPadding(5);
                table.addCell(cell);
            }

            Font cellFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9);

            for (Vente vente : ventes) {

                table.addCell(new PdfPCell(new Phrase(vente.getId().toString(), cellFont)));
                
                String dateStr = vente.getDateVente() != null ? 
                        vente.getDateVente().format(DATE_FORMATTER) : "-";
                table.addCell(new PdfPCell(new Phrase(dateStr, cellFont)));
                
                String nomClient = vente.getClient() != null ? 
                        vente.getClient().getNom() : "-";
                table.addCell(new PdfPCell(new Phrase(nomClient, cellFont)));

                String lot = "-";
                if (vente.getRecolte() != null && vente.getRecolte().getLot() != null) {
                    lot = vente.getRecolte().getLot().getCodeLot() != null ? 
                            vente.getRecolte().getLot().getCodeLot() : "-";
                }
                table.addCell(new PdfPCell(new Phrase(lot, cellFont)));

                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getPoidsVendu()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getPrixUnitaire()), cellFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", vente.getMontantTotal()), cellFont)));
                
                String statut = vente.getStatutVente() != null && vente.getStatutVente().getCode() != null ?
                        vente.getStatutVente().getCode().name() : "-";
                table.addCell(new PdfPCell(new Phrase(statut, cellFont)));
            }

            document.add(table);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footer =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        Color.GRAY);

        Paragraph footerP = new Paragraph("Document genere par AquaNova - Merci de votre confiance  - " + 
                LocalDateTime.now().format(DATE_FORMATTER), footer);
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