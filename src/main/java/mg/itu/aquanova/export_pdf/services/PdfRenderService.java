package mg.itu.aquanova.export_pdf.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfRenderService {

    private static final String DOSSIER_TEMPLATES = "pdf/";
    private static final String POLICE = "DejaVu Sans";

    private final SpringTemplateEngine templateEngine;

    public PdfRenderService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] rendre(String template, Map<String, Object> modele) {
        Context contexte = new Context();
        if (modele != null) {
            contexte.setVariables(modele);
        }

        String html = templateEngine.process(DOSSIER_TEMPLATES + template, contexte);
        return convertir(versXhtml(html));
    }

    private String versXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset("UTF-8");
        return document.html();
    }

    private byte[] convertir(String xhtml) {
        ByteArrayOutputStream sortie = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useFont(police("/fonts/DejaVuSans.ttf"), POLICE, 400,
                    BaseRendererBuilder.FontStyle.NORMAL, true);
            builder.useFont(police("/fonts/DejaVuSans-Bold.ttf"), POLICE, 700,
                    BaseRendererBuilder.FontStyle.NORMAL, true);
            builder.withHtmlContent(xhtml, null);
            builder.toStream(sortie);
            builder.run();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        }
    }

    private FSSupplier<InputStream> police(String chemin) {
        return () -> getClass().getResourceAsStream(chemin);
    }
}
