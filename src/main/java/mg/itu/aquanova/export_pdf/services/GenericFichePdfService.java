package mg.itu.aquanova.export_pdf.services;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

/**
 
 
 * Utilisation any am controller:
 *   List<String[]> data = new ArrayList<>();
 *   data.add(new String[]{"ID", "1"});
 *   data.add(new String[]{"Nom", "Jean"});
 *   
 *   pdfService.export(response, "fiche.pdf", "FICHE CLIENT", data);
 */
@Service
public class GenericFichePdfService {

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void export(HttpServletResponse response,
                       String filename,
                       String title,
                       List<String[]> data) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // En-tete
        addHeader(document);

        // Titre
        addTitle(document, title);

        // Date d'export
        addDate(document);

        // Tableau des donnes
        if (data != null && !data.isEmpty()) {
            addDataTable(document, data);
        }

        // Pied de page
        addFooter(document);

        document.close();
    }

    private void addHeader(Document document) throws Exception {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(0, 51, 102));
        Paragraph header = new Paragraph("AquaNova", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY);
        Paragraph subHeader = new Paragraph("Pisciculture AQUANOVA", subHeaderFont);
        subHeader.setAlignment(Element.ALIGN_CENTER);
        document.add(subHeader);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
    }

    private void addTitle(Document document, String title) throws Exception {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(0, 102, 204));
        Paragraph p = new Paragraph(title, titleFont);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));
    }

    private void addDate(Document document) throws Exception {
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph date = new Paragraph("Date d'export : " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        document.add(new Paragraph(" "));
    }

    private void addDataTable(Document document, List<String[]> data) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3});

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        for (String[] row : data) {
            String label = row[0];
            String value = row.length > 1 ? row[1] : "-";

            PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
            labelCell.setBackgroundColor(Color.LIGHT_GRAY);
            labelCell.setPadding(5);
            table.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
            valueCell.setPadding(5);
            table.addCell(valueCell);
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addFooter(Document document) throws Exception {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph footer = new Paragraph(
            "Document généré par AquaNova - " + LocalDateTime.now().format(DATE_FORMATTER),
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph thanks = new Paragraph("Merci de votre confiance !", footerFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);
    }
}