package mg.itu.aquanova.export_excel.models;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class ExcelResponses {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private ExcelResponses() {
    }

    public static ResponseEntity<byte[]> attachment(byte[] classeur, String nomFichier) {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setContentType(XLSX);
        entetes.setContentDisposition(ContentDisposition.attachment().filename(nomFichier).build());
        entetes.setContentLength(classeur.length);
        return ResponseEntity.ok().headers(entetes).body(classeur);
    }
}
