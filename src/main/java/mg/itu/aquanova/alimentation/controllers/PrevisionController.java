package mg.itu.aquanova.alimentation.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.alimentation.services.PrevisionService;

@Controller
public class PrevisionController {

    private final PrevisionService previsionService;

    public PrevisionController(PrevisionService previsionService) {
        this.previsionService = previsionService;
    }

    @GetMapping("/prevision")
    public String index(
            @RequestParam(required = false) Long alimentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "30") Integer horizonJours,
            Model model
    ) {
        LocalDate dateReference = date != null ? date : LocalDate.now();

        if (horizonJours == null || horizonJours <= 0) {
            horizonJours = 30;
        }

        try {
            model.addAttribute("previsions",
                    previsionService.getPrevisions(alimentId, dateReference, horizonJours));

            model.addAttribute("aliments",
                    previsionService.getAlimentsForFilter());

            model.addAttribute("stockTotal",
                    previsionService.getStockTotal(dateReference));

            model.addAttribute("dateReference", dateReference);
            model.addAttribute("alimentId", alimentId);
            model.addAttribute("horizonJours", horizonJours);

            return "alimentation/prevision/list";

        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("previsions", java.util.List.of());
            model.addAttribute("aliments", previsionService.getAlimentsForFilter());
            model.addAttribute("stockTotal", null);
            model.addAttribute("dateReference", dateReference);
            model.addAttribute("alimentId", alimentId);
            model.addAttribute("horizonJours", horizonJours);

            return "alimentation/prevision/list";
        }
    }

    @GetMapping("/alimentation/prevision")
    public String redirectToPrevision() {
        return "redirect:/prevision";
    }
}