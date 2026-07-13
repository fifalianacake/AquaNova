package mg.itu.aquanova.finance.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;
import mg.itu.aquanova.finance.dto.FinanceDashboardDTO;
import mg.itu.aquanova.finance.dto.RentabiliteLotDTO;
import mg.itu.aquanova.finance.services.FinanceDashboardService;
import mg.itu.aquanova.finance.services.RentabiliteLotService;
import mg.itu.aquanova.production.services.LotFilter;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final RentabiliteLotService rentabiliteLotService;
    private final FinanceDashboardService financeDashboardService;
    private final PdfRenderService pdfRenderService;

    public FinanceController(RentabiliteLotService rentabiliteLotService,
                             FinanceDashboardService financeDashboardService,
                             PdfRenderService pdfRenderService) {
        this.rentabiliteLotService = rentabiliteLotService;
        this.financeDashboardService = financeDashboardService;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {

        FinanceDashboardDTO dashboard = financeDashboardService.getDashboard(dateDebut, dateFin);
        model.addAttribute("dashboard", dashboard);
        return "finance/dashboard";
    }

    @GetMapping("/lots")
    public String listerLotsAvecMarge(
            @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable,
            @ModelAttribute("lotFilter") LotFilter lotFilter,
            Model model) {

        Page<RentabiliteLotDTO> page = rentabiliteLotService.lister(lotFilter, pageable);
        model.addAttribute("page", page);
        model.addAttribute("lots", page.getContent());
        model.addAttribute("lotFilter", lotFilter);
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "finance/lots/list";
    }

    @GetMapping("/lots/{id}/analyse")
    public String analyserLot(@PathVariable Long id, Model model) {
        model.addAttribute("lot", rentabiliteLotService.construirePourLot(id));
        return "finance/lots/analyse";
    }

    @GetMapping("/dashboard/export-pdf")
    public ResponseEntity<byte[]> exporterRapport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        FinanceDashboardDTO dashboard = financeDashboardService.getDashboard(dateDebut, dateFin);

        Map<String, Object> modele = new HashMap<>();
        modele.put("dashboard", dashboard);
        modele.put("sousTitre", "Période du " + dashboard.getDateDebut() + " au " + dashboard.getDateFin());

        byte[] pdf = pdfRenderService.rendre("finance-dashboard", modele);
        return PdfResponses.attachment(pdf, "rapport-financier-" + dashboard.getDateDebut() + ".pdf");
    }
}
