package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import mg.itu.aquanova.achat.dto.IntrantFilter;
import mg.itu.aquanova.achat.dto.MouvementStockIntrantFilter;
import mg.itu.aquanova.achat.models.CategorieIntrant;
import mg.itu.aquanova.achat.models.Intrant;
import mg.itu.aquanova.achat.models.MouvementStockIntrant;
import mg.itu.aquanova.achat.models.TypeMouvementIntrant;
import mg.itu.aquanova.achat.services.IntrantService;
import mg.itu.aquanova.achat.services.StockIntrantService;

@Controller
public class IntrantController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final IntrantService intrantService;
    private final StockIntrantService stockIntrantService;

    public IntrantController(IntrantService intrantService, StockIntrantService stockIntrantService) {
        this.intrantService = intrantService;
        this.stockIntrantService = stockIntrantService;
    }

    @GetMapping("/intrants")
    public String liste(
            @ModelAttribute("filter") IntrantFilter filter,
            @PageableDefault(size = 10, sort = "nom") Pageable pageable,
            Model model) {
        model.addAttribute("intrants", intrantService.lister(filter, pageable));
        addCommonAttributes(model);
        return "achat_depense/intrants/list";
    }

    @GetMapping("/intrants/new")
    public String formulaireCreation(Model model) {
        model.addAttribute("intrant", new Intrant());
        addCommonAttributes(model);
        return "achat_depense/intrants/form";
    }

    @PostMapping("/intrants")
    public String creer(@ModelAttribute Intrant intrant, Model model) {
        try {
            Intrant sauvegarde = intrantService.creer(intrant);
            return "redirect:/intrants/" + sauvegarde.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("intrant", intrant);
            addCommonAttributes(model);
            return "achat_depense/intrants/form";
        }
    }

    @GetMapping("/intrants/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("intrant", intrantService.trouverParId(id));
        model.addAttribute("stockActuel", stockIntrantService.calculerStock(id));
        return "achat_depense/intrants/detail";
    }

    @GetMapping("/intrants/{id}/edit")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("intrant", intrantService.trouverParId(id));
        addCommonAttributes(model);
        return "achat_depense/intrants/form";
    }

    @PostMapping("/intrants/{id}")
    public String modifier(@PathVariable Long id, @ModelAttribute Intrant intrant, Model model) {
        try {
            intrantService.modifier(id, intrant);
            return "redirect:/intrants/" + id;
        } catch (IllegalArgumentException ex) {
            intrant.setId(id);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("intrant", intrant);
            addCommonAttributes(model);
            return "achat_depense/intrants/form";
        }
    }

    @PostMapping("/intrants/{id}/delete")
    public String supprimerOuDesactiver(@PathVariable Long id) {
        intrantService.supprimerOuDesactiver(id);
        return "redirect:/intrants";
    }

    @GetMapping("/intrants/{id}/stock")
    public String ficheStock(
            @PathVariable Long id,
            @ModelAttribute("filter") MouvementStockIntrantFilter filter,
            @PageableDefault(size = 10, sort = "dateMouvement") Pageable pageable,
            Model model) {
        filter.setIntrantId(id);
        Page<MouvementStockIntrant> mouvements = stockIntrantService.listerMouvements(filter, pageable);

        model.addAttribute("intrant", intrantService.trouverParId(id));
        model.addAttribute("stockActuel", stockIntrantService.calculerStock(id));
        model.addAttribute("mouvements", mouvements);
        model.addAttribute("typesMouvement", TypeMouvementIntrant.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "achat_depense/intrants/stock";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("categoriesIntrant", CategorieIntrant.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }
}
