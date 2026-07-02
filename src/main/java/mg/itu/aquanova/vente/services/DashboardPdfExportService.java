package mg.itu.aquanova.vente.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
import mg.itu.aquanova.vente.dto.VenteStatsDto;

@Service
public class DashboardPdfExportService {

    public void exportDashboard(
            VenteStatsDto stats,
            Map<String, Double> ventesParClient,
            Map<String, Double> ventesParTypeClient,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=dashboard_ventes.pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        Color.BLACK);

        Paragraph p = new Paragraph("DASHBOARD COMMERCIAL", titre);

        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("AquaNova - Pisciculture"));
        document.add(new Paragraph(" "));

        // Période
        Font periodFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12,
                        new Color(0, 102, 204));

        Paragraph period = new Paragraph("Période du " + dateDebut.toString() + " au " + dateFin.toString(), periodFont);
        period.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(period);

        document.add(new Paragraph(" "));

        // Indicateurs clés
        Font indicatorTitle =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph indicatorsP = new Paragraph("INDICATEURS CLÉS", indicatorTitle);
        indicatorsP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(indicatorsP);

        document.add(new Paragraph(" "));

        PdfPTable indicatorsTable = new PdfPTable(4);
        indicatorsTable.setWidthPercentage(100);

        Font labelFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        11);

        Font valueFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 153, 0));

        // Chiffre d'affaires
        PdfPCell caCell = new PdfPCell();
        caCell.addElement(new Paragraph("Chiffre d'affaires", labelFont));
        caCell.addElement(new Paragraph(String.format("%.2f MGA", stats.getChiffreAffaires()), valueFont));
        caCell.setBackgroundColor(new Color(240, 248, 255));
        caCell.setPadding(10);
        caCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(caCell);

        // Volume écoulé
        PdfPCell volumeCell = new PdfPCell();
        volumeCell.addElement(new Paragraph("Volume écoulé", labelFont));
        volumeCell.addElement(new Paragraph(String.format("%.2f kg", stats.getVolumeEcoule()), valueFont));
        volumeCell.setBackgroundColor(new Color(240, 248, 255));
        volumeCell.setPadding(10);
        volumeCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(volumeCell);

        // Nombre de ventes
        PdfPCell nbCell = new PdfPCell();
        nbCell.addElement(new Paragraph("Nombre de ventes", labelFont));
        nbCell.addElement(new Paragraph(String.valueOf(stats.getNombreVentes()), valueFont));
        nbCell.setBackgroundColor(new Color(240, 248, 255));
        nbCell.setPadding(10);
        nbCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(nbCell);

        // Prix moyen au kg
        PdfPCell prixCell = new PdfPCell();
        prixCell.addElement(new Paragraph("Prix moyen au kg", labelFont));
        double prixMoyen = stats.getVolumeEcoule() > 0 ? stats.getChiffreAffaires() / stats.getVolumeEcoule() : 0;
        prixCell.addElement(new Paragraph(String.format("%.2f MGA/kg", prixMoyen), valueFont));
        prixCell.setBackgroundColor(new Color(240, 248, 255));
        prixCell.setPadding(10);
        prixCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(prixC);

        document.add(indicatorsTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Ventes par client
        Font clientTitle =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph clientP = new Paragraph("TOP CLIENTS", clientTitle);
        clientP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(clientP);

        document.add(new Paragraph(" "));

        if (ventesParClient.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucune donnée disponible.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Color.WHITE);

            PdfPCell h1 = new PdfPCell(new Phrase("Client", headerFont));
            h1.setBackgroundColor(new Color(0, 102, 204));
            clientTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Montant (MGA)", headerFont));
            h2.setBackgroundColor(new Color(0, 102, 204));
            clientTable.addCell(h2);

            Font dataFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11);

            for (Map.Entry<String, Double> entry : ventesParClient.entrySet()) {
                clientTable.addCell(new PdfPCell(new Phrase(entry.getKey(), dataFont)));
                clientTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", entry.getValue()), dataFont)));
            }

            document.add(clientTable);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Ventes par type client
        Font typeTitle =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph typeP = new Paragraph("RÉPARTITION PAR TYPE DE CLIENT", typeTitle);
        typeP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(typeP);

        document.add(new Paragraph(" "));

        if (ventesParTypeClient.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucune donnée disponible.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable typeTable = new PdfPTable(2);
            typeTable.setWidthPercentage(100);

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Color.WHITE);

            PdfPCell h1 = new PdfPCell(new Phrase("Type de client", headerFont));
            h1.setBackgroundColor(new Color(0, 102, 204));
            typeTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Montant (MGA)", headerFont));
            h2.setBackgroundColor(new Color(0, 102, 204));
            typeTable.addCell(h2);

            Font dataFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11);

            for (Map.Entry<String, Double> entry : ventesParTypeClient.entrySet()) {
                typeTable.addCell(new PdfPCell(new Phrase(entry.getKey(), dataFont)));
                typeTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", entry.getValue()), dataFont)));
            }

            document.add(typeTable);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footer =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        Color.GRAY);

        Paragraph footerP = new Paragraph("Document généré par AquaNova - " + LocalDateTime.now().toString(), footer);
        footerP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(footerP);

        document.close();

    }
}