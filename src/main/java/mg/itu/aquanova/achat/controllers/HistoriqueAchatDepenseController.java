package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

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
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        model.addAttribute("historiques", historiqueAchatDepenseService.lister(filter, pageable));
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
        model.addAttribute("operations", TypeOperation.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "achat_depense/historique/list";
    }
}
