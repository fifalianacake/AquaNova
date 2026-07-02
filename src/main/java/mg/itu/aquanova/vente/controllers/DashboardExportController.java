package mg.itu.aquanova.vente.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.dto.VenteStatsDto;
import mg.itu.aquanova.vente.services.DashboardPdfExportService;
import mg.itu.aquanova.vente.services.DashboardVenteService;

@RestController
@RequestMapping("/ventes")
public class DashboardExportController {

    private final DashboardVenteService dashboardService;
    private final DashboardPdfExportService pdfService;

    public DashboardExportController(
            DashboardVenteService dashboardService,
            DashboardPdfExportService pdfService) {

        this.dashboardService = dashboardService;
        this.pdfService = pdfService;
    }

    @GetMapping("/dashboard/export-pdf")
    public void exporterDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) Long clientId,
            HttpServletResponse response) throws Exception {

        VenteStatsDto stats = dashboardService.getChiffreAffaires(dateDebut, dateFin);
        Map<String, Double> ventesParClient = dashboardService.getTopClients(dateDebut, dateFin);
        Map<String, Double> ventesParTypeClient = dashboardService.getVentesParClientType(dateDebut, dateFin);

        pdfService.exportDashboard(stats, ventesParClient, ventesParTypeClient, dateDebut, dateFin, response);

    }
}