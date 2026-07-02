package mg.itu.aquanova.vente.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.ClientPdfExportService;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/clients")
public class ClientExportController {

    private final ClientService clientService;
    private final VenteService venteService;
    private final ClientPdfExportService pdfService;

    public ClientExportController(
            ClientService clientService,
            VenteService venteService,
            ClientPdfExportService pdfService) {

        this.clientService = clientService;
        this.venteService = venteService;
        this.pdfService = pdfService;
    }

    @GetMapping("/{id}/export-pdf")
    public void exporter(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {

        Client client = clientService.trouverParId(id);
        
      
        List<Vente> ventes = venteService.getByClient(id);

        pdfService.export(client, ventes, response);

    }
}