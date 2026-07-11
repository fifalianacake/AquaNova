package mg.itu.aquanova.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mg.itu.aquanova.dashboard.services.AccueilDashboardService;

/**
 * Page d'accueil : tableau de bord global de l'exploitation.
 */
@Controller
public class HelloController {

    private final AccueilDashboardService accueilDashboardService;

    public HelloController(AccueilDashboardService accueilDashboardService) {
        this.accueilDashboardService = accueilDashboardService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dashboard", accueilDashboardService.construire());
        return "index";
    }
}
