package mg.itu.aquanova.vente.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VentePdfExportService;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/ventes")
public class VenteExportController {

    private final VenteService venteService;
    private final VentePdfExportService pdfService;

    public VenteExportController(
            VenteService venteService,
            VentePdfExportService pdfService) {

        this.venteService = venteService;
        this.pdfService = pdfService;
    }

    @GetMapping("/{id}/export-pdf")
    public void exporter(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {

        Vente vente = venteService.trouverParId(id);

        pdfService.export(vente, response);

    }
}