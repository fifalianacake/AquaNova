package mg.itu.aquanova.vente.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.vente.dto.GraphDataDto;
import mg.itu.aquanova.vente.dto.PerformanceClientDto;
import mg.itu.aquanova.vente.dto.VenteStatsDto;
import mg.itu.aquanova.vente.dto.VolumeEcouleDto;
import mg.itu.aquanova.vente.services.DashboardVenteService;

@Controller
@RequestMapping("/ventes/dashboard")
public class DashboardVenteController {

    private final DashboardVenteService dashboardVenteService;

    public DashboardVenteController(DashboardVenteService dashboardVenteService) {
        this.dashboardVenteService = dashboardVenteService;
    }

    @GetMapping
    public String afficherDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {

        if (debut == null) debut = LocalDate.now().withDayOfMonth(1);
        if (fin == null)   fin   = LocalDate.now();

        // Stats globales
        VenteStatsDto stats = this.dashboardVenteService.getStats(debut, fin);
        List<PerformanceClientDto> topClients = this.dashboardVenteService.getTopClients(debut, fin);
        List<VolumeEcouleDto> parLot = this.dashboardVenteService.getVentesParLotOuRecolte(debut, fin);

        // Données graphiques
        GraphDataDto caParJour    = this.dashboardVenteService.getCaParJour(debut, fin);
        GraphDataDto volumeParLot = this.dashboardVenteService.getVolumeParLot(debut, fin);
        GraphDataDto caParClient  = this.dashboardVenteService.getCaParClient(debut, fin);
        GraphDataDto top5         = this.dashboardVenteService.getTop5ClientsParCa(debut, fin);

        // Tableaux
        model.addAttribute("stats", stats);
        model.addAttribute("topClients", topClients);
        model.addAttribute("parLot", parLot);
        model.addAttribute("debut", debut);
        model.addAttribute("fin", fin);

        // JSON pour Chart.js
        model.addAttribute("caParJourLabels",    this.toJsonStringArray(caParJour.getLabels()));
        model.addAttribute("caParJourValues",    this.toJsonNumberArray(caParJour.getValues()));
        model.addAttribute("volumeParLotLabels", this.toJsonStringArray(volumeParLot.getLabels()));
        model.addAttribute("volumeParLotValues", this.toJsonNumberArray(volumeParLot.getValues()));
        model.addAttribute("caClientLabels",     this.toJsonStringArray(caParClient.getLabels()));
        model.addAttribute("caClientValues",     this.toJsonNumberArray(caParClient.getValues()));
        model.addAttribute("top5Labels",         this.toJsonStringArray(top5.getLabels()));
        model.addAttribute("top5Values",         this.toJsonNumberArray(top5.getValues()));

        return "ventes/dashboard";
    }

    private String toJsonStringArray(List<String> values) {
        if (values == null) return "[]";

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) json.append(',');
            json.append('"').append(this.escapeJson(values.get(i))).append('"');
        }
        return json.append(']').toString();
    }

    private String toJsonNumberArray(List<Double> values) {
        if (values == null) return "[]";

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) json.append(',');

            Double value = values.get(i);
            json.append(value == null || value.isNaN() || value.isInfinite() ? "null" : value);
        }
        return json.append(']').toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
