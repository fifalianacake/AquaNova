package mg.itu.aquanova.vente.services;

import java.awt.Color;

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
public class VentePdfExportService {

    public void export(Vente vente,
                       HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=facture_vente_" + vente.getId() + ".pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titre =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        Color.BLACK);

        Paragraph p = new Paragraph("FACTURE DE VENTE N° " + vente.getId(), titre);

        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("AquaNova - Pisciculture"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100);

        ajouterLigne(table, "ID Vente", vente.getId().toString());
        ajouterLigne(table, "Date vente", vente.getDateVente().toString());
        ajouterLigne(table, "Client", vente.getClient().getNom());
        ajouterLigne(table, "Type Client", vente.getClient().getTypeClient().getLibelle());
        ajouterLigne(table, "Contact", vente.getClient().getContact() != null ? vente.getClient().getContact() : "-");
        ajouterLigne(table, "Email", vente.getClient().getEmail() != null ? vente.getClient().getEmail() : "-");
        ajouterLigne(table, "Récolte", vente.getRecolte().getId().toString());
        ajouterLigne(table, "Lot", vente.getRecolte().getLot() != null ? vente.getRecolte().getLot().getCodeLot() : "-");
        ajouterLigne(table, "Espèce", vente.getRecolte().getLot() != null ? vente.getRecolte().getLot().getEspece() : "-");
        ajouterLigne(table, "Poids vendu", vente.getPoidsVendu() + " kg");
        ajouterLigne(table, "Effectif", vente.getEffectifVendu() != null ? vente.getEffectifVendu() + " unités" : "-");
        ajouterLigne(table, "Prix unitaire", vente.getPrixUnitaire() + " MGA/kg");
        ajouterLigne(table, "Montant total", vente.getMontantTotal() + " MGA");
        ajouterLigne(table, "Statut", vente.getStatutVente().getLibelle());
        ajouterLigne(table, "Observation", vente.getObservation() == null ? "-" : vente.getObservation());

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footer =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        Color.GRAY);

        Paragraph footerP = new Paragraph("Document généré par AquaNova - Merci de votre confiance !", footer);
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