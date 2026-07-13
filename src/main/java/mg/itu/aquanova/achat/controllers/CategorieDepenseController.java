package mg.itu.aquanova.achat.controllers;

import java.util.List;

// import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.services.AchatService;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.DepenseService;


@Controller
public class CategorieDepenseController {

    private final CategorieDepenseService categorieDepenseService;
    private final DepenseService depenseService;
    private final AchatService achatService;

    public CategorieDepenseController(
        CategorieDepenseService categorieDepenseService, 
        DepenseService depenseService,
        AchatService achatService
    ) {
        this.categorieDepenseService = categorieDepenseService;
        this.depenseService = depenseService;
        this.achatService = achatService;
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories-depenses")
    public String liste(
            Model model) {
        model.addAttribute("categories", categorieDepenseService.listerTous());
        
        return "achat_depense/categories_depenses/list";
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories-depenses/new")
    public String formulaireCreation(Model model) {
        model.addAttribute("categorie", new CategorieDepense());
        return "achat_depense/categories_depenses/form";
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories-depenses")
    public String createCategorie(@RequestBody CategorieDepense categorieDepense) {
        categorieDepenseService.create(categorieDepense);
        return "redirect:/categories-depenses";
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories-depenses/{id}/edit")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("categorie", categorieDepenseService.trouverParId(id));
        return "achat_depense/categories_depenses/form";
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories-depenses/{id}")
    public String modifier(@PathVariable Long id, @ModelAttribute CategorieDepense categorieDepense, Model model) {
        try {
            categorieDepenseService.modifier(id, categorieDepense);
            return "redirect:/categories-depenses";
        } catch (IllegalArgumentException ex) {
            categorieDepense.setId(id);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("categorie", categorieDepense);
            return "achat_depense/categories_depenses/form";
        }
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories-depenses/{id}/delete")
    public String deleteCategorie(@PathVariable Long id, Model model) {
        if(!depenseService.estDejaUtilise(id) && !achatService.estDejaUtilise(id)) {
            categorieDepenseService.delete(id);
        } else {
           model.addAttribute("error", "Impossible de supprimer la catégorie : elle est actuellement liée à des informations enregistrées.");
        }
        return "redirect:/categories-depenses";
    }
    
}
