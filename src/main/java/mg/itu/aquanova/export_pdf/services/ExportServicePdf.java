package mg.itu.aquanova.export_pdf.services;

import java.io.OutputStream;
import java.lang.reflect.Field;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import mg.itu.aquanova.export_pdf.models.*;

@Service
public class ExportServicePdf {

    // 1. Exporter une FICHE dynamiquement
    public void genererFiche(FichePdf fiche, OutputStream out) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Object obj = fiche.getMainObject();
        if (obj != null) {
            // Récupère le nom de la classe (ex: "Fournisseur")
            String className = obj.getClass().getSimpleName();
            document.add(new Paragraph("FICHE : " + className.toUpperCase()));
            document.add(new Paragraph("--------------------------------------------------"));

            // Inspection par Réflexion de l'objet principal
            Class<?> clazz = obj.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Permet de lire les attributs privés (private)
                
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);

                // On ignore les listes ou collections internes pour ne pas polluer la fiche principale
                if (fieldValue instanceof java.util.Collection) {
                    continue;
                }

                document.add(new Paragraph(fieldName + " : " + (fieldValue != null ? fieldValue.toString() : "")));
            }
        }

        // Gestion de la sous-liste (ex: Historique des achats)
        if (fiche.getSubList() != null && !fiche.getSubList().isEmpty()) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph(fiche.getSubListTitle().toUpperCase()));
            document.add(new Paragraph("--------------------------------------------------"));

            for (Object subItem : fiche.getSubList()) {
                // Pour la sous-liste simplifiée, on affiche par défaut l'identifiant ou un champ clé si présent
                document.add(new Paragraph("- " + extraireInfoPrincipale(subItem)));
            }
        }

        document.close();
    }

    private String extraireInfoPrincipale(Object obj) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idVal = idField.get(obj);
            
            try {
                Field nameField = obj.getClass().getDeclaredField("reference");
                nameField.setAccessible(true);
                return "ID: " + idVal + " | Réf: " + nameField.get(obj);
            } catch (NoSuchFieldException e) {
                return "ID: " + idVal;
            }
        } catch (Exception e) {
            return obj.toString(); 
        }
    }
}