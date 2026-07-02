package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.itu.aquanova.achat.dto.AchatIntrantFilter;
import mg.itu.aquanova.achat.dto.AchatIntrantForm;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.services.AchatIntrantService;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.achat.services.IntrantService;

@Controller
public class AchatController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final AchatIntrantService achatIntrantService;
    private final FournisseurService fournisseurService;
    private final CategorieDepenseService categorieDepenseService;
    private final IntrantService intrantService;

    public AchatController(
            AchatIntrantService achatIntrantService,
            FournisseurService fournisseurService,
            CategorieDepenseService categorieDepenseService,
            IntrantService intrantService) {
        this.achatIntrantService = achatIntrantService;
        this.fournisseurService = fournisseurService;
        this.categorieDepenseService = categorieDepenseService;
        this.intrantService = intrantService;
    }

    @GetMapping("/achats")
    public String liste(
            @ModelAttribute("filter") AchatIntrantFilter filter,
            @PageableDefault(size = 10, sort = "dateAchat") Pageable pageable,
            Model model) {
        model.addAttribute("achats", achatIntrantService.listerAchatsIntrants(filter, pageable));
        addListAttributes(model);
        return "achat_depense/achats/list";
    }

    @GetMapping("/achats/intrants/new")
    public String formulaireAchatIntrant(Model model) {
        model.addAttribute("achatIntrantForm", new AchatIntrantForm());
        addFormAttributes(model);
        return "achat_depense/achats/form-intrant";
    }

    @PostMapping("/achats/intrants")
    public String creerAchatIntrant(
            @ModelAttribute("achatIntrantForm") AchatIntrantForm form,
            Model model) {
        try {
            var achat = achatIntrantService.creerAchatIntrant(form);
            return "redirect:/achats/" + achat.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("achatIntrantForm", form);
            addFormAttributes(model);
            return "achat_depense/achats/form-intrant";
        }
    }

    @GetMapping("/achats/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("achat", achatIntrantService.trouverParId(id));
        return "achat_depense/achats/detail";
    }

    @PostMapping("/achats/{id}/valider")
    public String valider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            achatIntrantService.validerAchat(id);
            redirectAttributes.addFlashAttribute("success", "Achat validé et stock intrant alimenté.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/achats/" + id;
    }

    @PostMapping("/achats/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            achatIntrantService.annulerAchat(id);
            redirectAttributes.addFlashAttribute("success", "Achat annulé.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/achats/" + id;
    }

    private void addListAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerTous());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerCategoriesAchatIntrants());
        model.addAttribute("intrants", intrantService.listerTous());
        model.addAttribute("statutsAchat", StatutAchat.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerCategoriesAchatIntrants());
        model.addAttribute("intrants", intrantService.listerActifs());
    }
}
