package mg.itu.aquanova.achat.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.achat.dto.HistoriqueAchatDepenseFilter;
import mg.itu.aquanova.achat.dto.TypeOperation;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.achat.services.HistoriqueAchatDepenseService;

@Controller
@RequestMapping("/achats-depenses")
public class HistoriqueAchatDepenseController {

    private final HistoriqueAchatDepenseService historiqueAchatDepenseService;
    private final FournisseurService fournisseurService;
    private final CategorieDepenseService categorieDepenseService;

    public HistoriqueAchatDepenseController(
            HistoriqueAchatDepenseService historiqueAchatDepenseService,
            FournisseurService fournisseurService,
            CategorieDepenseService categorieDepenseService) {
        this.historiqueAchatDepenseService = historiqueAchatDepenseService;
        this.fournisseurService = fournisseurService;
        this.categorieDepenseService = categorieDepenseService;
    }

    @GetMapping("/historique")
    public String afficherHistorique(
            @ModelAttribute("filter") HistoriqueAchatDepenseFilter filter,
            Model model) {
        model.addAttribute("historiques", historiqueAchatDepenseService.rechercher(filter));
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
        model.addAttribute("operations", TypeOperation.values());
        return "achat_depense/historique/list";
    }
}
