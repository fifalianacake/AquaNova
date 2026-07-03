package mg.itu.aquanova.vente.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.vente.dto.PerformanceClientDto;
import mg.itu.aquanova.vente.dto.VenteStatsDto;
import mg.itu.aquanova.vente.services.DashboardVenteService;

@RestController
@RequestMapping("/ventes")
public class DashboardExportController {

    private final DashboardVenteService dashboardService;
    private final PdfExportService pdfService;

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DashboardExportController(DashboardVenteService dashboardService, 
                                     PdfExportService pdfService) {
        this.dashboardService = dashboardService;
        this.pdfService = pdfService;
    }

   
    @GetMapping("/dashboard/export-pdf")
    public ResponseEntity<byte[]> exporterDashboard(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin) {

        VenteStatsDto stats = dashboardService.getStats(dateDebut, dateFin);
        List<PerformanceClientDto> topClients = dashboardService.getTopClients(dateDebut, dateFin);

       
        List<List<String>> lignes = new ArrayList<>();
        
        // Indicateurs cles
        lignes.add(List.of("--- INDICATEURS CLÉS ---", ""));
        lignes.add(List.of("Chiffre d'affaires", 
                stats.getChiffreAffaires() != null ? 
                String.format("%,.2f MGA", stats.getChiffreAffaires()) : "-"));
        lignes.add(List.of("Volume écoulé", 
                stats.getVolumeEcoule() != null ? 
                String.format("%,.2f kg", stats.getVolumeEcoule()) : "-"));
        lignes.add(List.of("Nombre de ventes", 
                stats.getNombreVentes() != null ? 
                String.valueOf(stats.getNombreVentes()) : "-"));
        lignes.add(List.of("Prix moyen au kg", 
                stats.getPrixMoyenKg() != null ? 
                String.format("%,.2f MGA/kg", stats.getPrixMoyenKg()) : "-"));
        
        lignes.add(List.of("", "")); // Separateur
        
        // Top Clients
        lignes.add(List.of("--- TOP CLIENTS ---", ""));
        if (topClients != null && !topClients.isEmpty()) {
            for (PerformanceClientDto dto : topClients) {
                lignes.add(List.of(
                    dto.getClientNom() != null ? dto.getClientNom() : "-",
                    dto.getChiffreAffaires() != null ? 
                        String.format("%,.2f MGA", dto.getChiffreAffaires()) : "-"
                ));
            }
        } else {
            lignes.add(List.of("Aucun client", "-"));
        }

        ListePdfData data = ListePdfData.of("DASHBOARD COMMERCIAL")
            .filtre("Période", "Du " + dateDebut.format(DATE_FORMATTER) + 
                    " au " + dateFin.format(DATE_FORMATTER))
            .colonnes(List.of("Indicateur", "Valeur"))
            .lignes(lignes)
            .total("Total CA", stats.getChiffreAffaires() != null ? 
                    String.format("%,.2f MGA", stats.getChiffreAffaires()) : "-")
            .total("Total Volume", stats.getVolumeEcoule() != null ? 
                    String.format("%,.2f kg", stats.getVolumeEcoule()) : "-");

        byte[] pdf = pdfService.genererListe(data);
        return PdfResponses.attachment(pdf, "dashboard_ventes_" + 
                dateDebut + "_" + dateFin + ".pdf");
    }
}