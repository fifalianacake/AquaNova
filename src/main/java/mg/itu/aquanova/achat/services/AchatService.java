package mg.itu.aquanova.achat.services;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.repositories.AchatRepository;

@Service
public class AchatService {

    private final AchatRepository achatRepository;

    public AchatService(
        AchatRepository achatRepository
    ) {
        this.achatRepository = achatRepository;
    }

    public boolean estDejaUtilise(Long id) {
        return this.achatRepository.existsByCategorieDepenseId(id);
    }

    public byte[] intoPdfAlevin(Achat achat) {
        // Créer le flux de sortie en mémoire
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Initialiser le document PDF
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            
            // Ouvrir le document pour commencer l'écriture
            document.open();
            
            // --- Définition des Polices ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

            // --- Titre du PDF ---
            Paragraph title = new Paragraph("FACTURE D'ACHAT D'ALEVINS N° " + achat.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // --- Infos Générales ---
            document.add(new Paragraph("Date de l'achat : " + achat.getDateAchat(), normalFont));
            document.add(new Paragraph("Référence Facture : " + (achat.getReferenceFacture() != null ? achat.getReferenceFacture() : "N/A"), normalFont));
            document.add(new Paragraph("-------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" ")); // Ligne vide pour l'espace

            // --- Tableau des Détails ---
            // Création d'un tableau à 4 colonnes (Désignation, Quantité/Effectif, Prix Unitaire, Total)
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Entêtes du tableau
            String[] headers = {"Désignation / Espèce", "Effectif", "Prix Unitaire", "Montant Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(0, 123, 255));
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Contenu du tableau
            // Note : Si vous n'avez pas de sous-liste et que l'Achat contient directement les infos, adaptez les variables :
            table.addCell(new PdfPCell(new Phrase("Alevins " + achat.getEspece().getNom(), normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(achat.getEffectif()), normalFont)));
            table.addCell(new PdfPCell(new Phrase(achat.getPrixUnitaire() + " €", normalFont)));
            table.addCell(new PdfPCell(new Phrase(achat.getMontantTotal() + " €", normalFont)));

            document.add(table);
            document.add(new Paragraph(" "));

            // --- Total Global ---
            Paragraph total = new Paragraph("Montant Total à Payer : " + achat.getMontantTotal() + " €", totalFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // Fermer le document
            document.close();
            
            // Retourner le fichier en tableau d'octets pour votre contrôleur web
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF avec OpenPDF", e);
        }
    }

}
