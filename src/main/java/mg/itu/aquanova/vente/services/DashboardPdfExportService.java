package mg.itu.aquanova.vente.services;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import mg.itu.aquanova.vente.dto.PerformanceClientDto;
import mg.itu.aquanova.vente.dto.VenteStatsDto;
import mg.itu.aquanova.vente.dto.VolumeEcouleDto;

@Service
public class DashboardPdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void exportDashboard(
            VenteStatsDto stats,
            List<PerformanceClientDto> topClients,
            List<VolumeEcouleDto> ventesParLot,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=dashboard_ventes_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

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

        String periode = "Du " + dateDebut.format(DATE_FORMATTER) + " au " + dateFin.format(DATE_FORMATTER);
        Paragraph period = new Paragraph(periode, periodFont);
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
        Double ca = stats.getChiffreAffaires() != null ? stats.getChiffreAffaires() : 0.0;
        caCell.addElement(new Paragraph(String.format("%.2f MGA", ca), valueFont));
        caCell.setBackgroundColor(new Color(240, 248, 255));
        caCell.setPadding(10);
        caCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(caCell);

        // Volume ecoule
        PdfPCell volumeCell = new PdfPCell();
        volumeCell.addElement(new Paragraph("Volume écoulé", labelFont));
        Double volume = stats.getVolumeEcoule() != null ? stats.getVolumeEcoule() : 0.0;
        volumeCell.addElement(new Paragraph(String.format("%.2f kg", volume), valueFont));
        volumeCell.setBackgroundColor(new Color(240, 248, 255));
        volumeCell.setPadding(10);
        volumeCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(volumeCell);

        // Nombre de ventes
        PdfPCell nbCell = new PdfPCell();
        nbCell.addElement(new Paragraph("Nombre de ventes", labelFont));
        Long nb = stats.getNombreVentes() != null ? stats.getNombreVentes() : 0L;
        nbCell.addElement(new Paragraph(String.valueOf(nb), valueFont));
        nbCell.setBackgroundColor(new Color(240, 248, 255));
        nbCell.setPadding(10);
        nbCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(nbCell);

        // Prix moyen au kg
        PdfPCell prixCell = new PdfPCell();
        prixCell.addElement(new Paragraph("Prix moyen au kg", labelFont));
        Double prixMoyen = stats.getPrixMoyenKg() != null ? stats.getPrixMoyenKg() : 0.0;
        prixCell.addElement(new Paragraph(String.format("%.2f MGA/kg", prixMoyen), valueFont));
        prixCell.setBackgroundColor(new Color(240, 248, 255));
        prixCell.setPadding(10);
        prixCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        indicatorsTable.addCell(prixCell);

        document.add(indicatorsTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Top Clients
        Font clientTitle =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph clientP = new Paragraph("TOP CLIENTS", clientTitle);
        clientP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(clientP);

        document.add(new Paragraph(" "));

        if (topClients == null || topClients.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucune donnée disponible.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable clientTable = new PdfPTable(4);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{2, 1, 1.5f, 1.5f});

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Color.WHITE);

            String[] headers = {"Client", "Nb Ventes", "Volume (kg)", "CA (MGA)"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(0, 102, 204));
                cell.setPadding(5);
                clientTable.addCell(cell);
            }

            Font dataFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11);

            for (PerformanceClientDto dto : topClients) {
                clientTable.addCell(new PdfPCell(new Phrase(dto.getClientNom(), dataFont)));
                clientTable.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getNombreVentes()), dataFont)));
                clientTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", dto.getVolumeAchete()), dataFont)));
                clientTable.addCell(new PdfPCell(new Phrase(String.format("%.2f", dto.getChiffreAffaires()), dataFont)));
            }

            document.add(clientTable);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // Ventes par lot
        Font lotTitle =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        new Color(0, 102, 204));

        Paragraph lotP = new Paragraph("VENTES PAR LOT / RÉCOLTE", lotTitle);
        lotP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(lotP);

        document.add(new Paragraph(" "));

        if (ventesParLot == null || ventesParLot.isEmpty()) {

            Font emptyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12,
                            Color.GRAY);

            Paragraph empty = new Paragraph("Aucune donnée disponible.", emptyFont);
            empty.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(empty);

        } else {

            PdfPTable lotTable = new PdfPTable(5);
            lotTable.setWidthPercentage(100);
            lotTable.setWidths(new float[]{1.5f, 1.5f, 1.5f, 1, 1.5f});

            Font headerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            Color.WHITE);

            String[] headers = {"Lot", "Récolte", "Poids (kg)", "Effectif", "Montant (MGA)"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(0, 102, 204));
                cell.setPadding(5);
                lotTable.addCell(cell);
            }

            Font dataFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10);

            for (VolumeEcouleDto dto : ventesParLot) {
                lotTable.addCell(new PdfPCell(new Phrase(
                        dto.getLotNom() != null ? dto.getLotNom() : "-", dataFont)));
                lotTable.addCell(new PdfPCell(new Phrase(
                        dto.getRecolteReference() != null ? dto.getRecolteReference() : "-", dataFont)));
                lotTable.addCell(new PdfPCell(new Phrase(
                        String.format("%.2f", dto.getPoidsVendu()), dataFont)));
                lotTable.addCell(new PdfPCell(new Phrase(
                        String.valueOf(dto.getEffectifVendu()), dataFont)));
                lotTable.addCell(new PdfPCell(new Phrase(
                        String.format("%.2f", dto.getMontantTotal()), dataFont)));
            }

            document.add(lotTable);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footer =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        Color.GRAY);

        Paragraph footerP = new Paragraph("Document genere par AquaNova - Merci de votre confiance ! " + 
                LocalDateTime.now().format(DATE_FORMATTER), footer);
        footerP.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(footerP);

        document.close();

    }
}