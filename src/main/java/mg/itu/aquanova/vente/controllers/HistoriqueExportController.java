package mg.itu.aquanova.vente.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.HistoriquePdfExportService;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/ventes")
public class HistoriqueExportController {

    private final VenteService venteService;
    private final HistoriquePdfExportService pdfService;

    public HistoriqueExportController(
            VenteService venteService,
            HistoriquePdfExportService pdfService) {

        this.venteService = venteService;
        this.pdfService = pdfService;
    }

    @GetMapping("/historique/export-pdf")
    public void exporterHistorique(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String statut,
            HttpServletResponse response) throws Exception {

        String clientNom = null;

        // RECUPERER avec filtres
        List<Vente> ventes = venteService.search(
                null, // id
                null, // client (String)
                null, // recolteId
                null, // lotId
                dateDebut != null ? dateDebut.toLocalDate() : null,
                dateFin != null ? dateFin.toLocalDate() : null,
                null  // statutId
        );

        if (clientId != null) {
           
            clientNom = clientService.trouverParId(clientId).getNom();
        }

        pdfService.exportHistorique(ventes, dateDebut, dateFin, clientNom, response);

    }
}