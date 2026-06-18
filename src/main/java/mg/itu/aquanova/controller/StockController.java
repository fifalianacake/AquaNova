package mg.itu.aquanova.controller;

import mg.itu.aquanova.entity.Aliment;
import mg.itu.aquanova.entity.MouvementStock;
import mg.itu.aquanova.service.AlimentService;
import mg.itu.aquanova.service.MouvementService;
import mg.itu.aquanova.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;
    private final AlimentService alimentService;
    private final MouvementService mouvementService;

    public StockController(StockService stockService,
                            AlimentService alimentService,
                            MouvementService mouvementService) {
        this.stockService = stockService;
        this.alimentService = alimentService;
        this.mouvementService = mouvementService;
    }

    
    @GetMapping
    public String getEtatStocks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String alimentNom,
            @RequestParam(required = false, defaultValue = "ALL") String alerte,
            Model model) {

        LocalDate dateRecherche = (date != null) ? date : LocalDate.now();

        List<Object[]> stocks = stockService.getStockAtDate(dateRecherche);

        List<Object[]> stocksFiltres = stocks.stream()
                .filter(row -> {
                    Aliment aliment = (Aliment) row[0];
                    if (alimentNom != null && !alimentNom.isBlank()) {
                        return aliment.getNom().toLowerCase().contains(alimentNom.toLowerCase());
                    }
                    return true;
                })
                .filter(row -> {
                    if ("ALL".equalsIgnoreCase(alerte)) {
                        return true;
                    }
                    Aliment aliment = (Aliment) row[0];
                    boolean enAlerte = stockService.isAlerte(aliment, dateRecherche);
                    if ("OUI".equalsIgnoreCase(alerte)) {
                        return enAlerte;
                    }
                    if ("NON".equalsIgnoreCase(alerte)) {
                        return !enAlerte;
                    }
                    return true;
                })
                .toList();

        model.addAttribute("stocks", stocksFiltres);
        model.addAttribute("date", dateRecherche);
        model.addAttribute("alimentNom", alimentNom);
        model.addAttribute("alerte", alerte);

        model.addAttribute("nbAlertes", stockService.countAlertes(dateRecherche));
        model.addAttribute("stockTotal", stockService.totalStock(dateRecherche));

        return "stocks/liste";
    }


    @GetMapping("/{alimentId}")
    public String getFicheStock(
            @PathVariable Long alimentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            RedirectAttributes redirectAttributes) {

        LocalDate dateRecherche = (date != null) ? date : LocalDate.now();

        try {
            Aliment aliment = alimentService.findById(alimentId)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + alimentId));

            BigDecimal stock = stockService.getStockByAlimentAndDate(alimentId, dateRecherche);
            boolean enAlerte = stockService.isAlerte(aliment, dateRecherche);

            List<MouvementStock> historique = mouvementService.getRecentByAlimentAndDate(alimentId, dateRecherche, 10);

            model.addAttribute("aliment", aliment);
            model.addAttribute("stock", stock);
            model.addAttribute("alerte", enAlerte);
            model.addAttribute("date", dateRecherche);
            model.addAttribute("historique", historique);

            return "stocks/fiche";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/stocks?date=" + dateRecherche;
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleBadPathVariable(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erreur", "URL invalide: la liste des stocks se trouve a /stocks");
        return "redirect:/stocks";
    }
}