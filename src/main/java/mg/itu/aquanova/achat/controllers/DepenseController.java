package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.itu.aquanova.achat.dto.DepenseFilter;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.DepenseService;

@Controller
@RequestMapping("/depenses")
public class DepenseController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final DepenseService depenseService;
    private final CategorieDepenseService categorieDepenseService;

    public DepenseController(
            DepenseService depenseService,
            CategorieDepenseService categorieDepenseService) {
        this.depenseService = depenseService;
        this.categorieDepenseService = categorieDepenseService;
    }

    @GetMapping
    public String liste(
            @ModelAttribute("filter") DepenseFilter filter,
            @PageableDefault(size = 10, sort = "dateDepense") Pageable pageable,
            Model model) {
        model.addAttribute("depenses", depenseService.lister(filter, pageable));
        model.addAttribute("totalFiltre", depenseService.calculerTotalFiltre(filter));
        addListAttributes(model);
        return "achat_depense/depenses/list";
    }

    @GetMapping("/new")
    public String formulaireCreation(Model model) {
        model.addAttribute("depense", creerDepenseFormulaire());
        addFormAttributes(model);
        return "achat_depense/depenses/form";
    }

    @PostMapping
    public String creer(
            @ModelAttribute("depense") Depense depense,
            Model model) {
        try {
            Depense sauvegardee = depenseService.enregistrer(depense);
            return "redirect:/depenses/" + sauvegardee.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            preparerDepenseFormulaire(depense);
            model.addAttribute("depense", depense);
            addFormAttributes(model);
            return "achat_depense/depenses/form";
        }
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("depense", depenseService.trouverParId(id));
        return "achat_depense/depenses/detail";
    }

    @GetMapping("/{id}/edit")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("depense", depenseService.trouverParId(id));
        addFormAttributes(model);
        return "achat_depense/depenses/form";
    }

    @PostMapping("/{id}")
    public String modifier(
            @PathVariable Long id,
            @ModelAttribute("depense") Depense depense,
            Model model) {
        try {
            depense.setId(id);
            Depense sauvegardee = depenseService.enregistrer(depense);
            return "redirect:/depenses/" + sauvegardee.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            preparerDepenseFormulaire(depense);
            model.addAttribute("depense", depense);
            addFormAttributes(model);
            return "achat_depense/depenses/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            depenseService.supprimer(id);
            redirectAttributes.addFlashAttribute("success", "Dépense supprimée.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/depenses";
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@ModelAttribute("filter") DepenseFilter filter) {
        byte[] pdf = depenseService.exporterPdf(filter);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("depenses.pdf").build().toString())
                .body(pdf);
    }

    private void addListAttributes(Model model) {
        model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
    }

    private Depense creerDepenseFormulaire() {
        Depense depense = new Depense();
        preparerDepenseFormulaire(depense);
        return depense;
    }

    private void preparerDepenseFormulaire(Depense depense) {
        if (depense.getCategorieDepense() == null) {
            depense.setCategorieDepense(new CategorieDepense());
        }
        if (depense.getPaiements() == null || depense.getPaiements().isEmpty()) {
            depense.addPaiement(new mg.itu.aquanova.achat.models.DepensePaiement());
        }
    }
}
