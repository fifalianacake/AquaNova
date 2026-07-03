package mg.itu.aquanova.export_pdf.models;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Évite de répéter la construction des en-têtes HTTP (Content-Type, nom de fichier...)
 * dans chaque contrôleur qui propose un export PDF.
 *
 * Usage dans un contrôleur :
 *
 * @GetMapping("/achats/{id}/export-pdf")
 * public ResponseEntity<byte[]> exporterPdf(@PathVariable Long id) {
 *     byte[] pdf = pdfExportService.genererFiche(...);
 *     return PdfResponses.attachment(pdf, "achat-" + id + ".pdf");
 * }
 */
public final class PdfResponses {

    private PdfResponses() {
    }

    public static ResponseEntity<byte[]> attachment(byte[] pdf, String nomFichier) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomFichier).build());
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
