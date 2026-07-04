package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.itu.aquanova.achat.dto.AchatAlevinFilter;
import mg.itu.aquanova.achat.dto.AchatAlevinForm;
import mg.itu.aquanova.achat.dto.AchatIntrantFilter;
import mg.itu.aquanova.achat.dto.AchatIntrantForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.services.AchatAlevinService;
import mg.itu.aquanova.achat.services.AchatIntrantService;
import mg.itu.aquanova.achat.services.AchatService;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.achat.services.IntrantService;
import mg.itu.aquanova.referentiel.services.EspecesService;

@Controller
public class AchatController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final AchatService achatService;
    private final AchatIntrantService achatIntrantService;
    private final AchatAlevinService achatAlevinService;
    private final FournisseurService fournisseurService;
    private final CategorieDepenseService categorieDepenseService;
    private final IntrantService intrantService;
    private final EspecesService especesService;

    public AchatController(
            AchatService achatService,
            AchatIntrantService achatIntrantService,
            AchatAlevinService achatAlevinService,
            FournisseurService fournisseurService,
            CategorieDepenseService categorieDepenseService,
            IntrantService intrantService,
            EspecesService especesService) {
        this.achatService = achatService;
        this.achatIntrantService = achatIntrantService;
        this.achatAlevinService = achatAlevinService;
        this.fournisseurService = fournisseurService;
        this.categorieDepenseService = categorieDepenseService;
        this.intrantService = intrantService;
        this.especesService = especesService;
    }

    @GetMapping("/achats")
    public String liste(
            @ModelAttribute("filter") AchatIntrantFilter filter,
            @ModelAttribute("filterAlevin") AchatAlevinFilter filterAlevin,
            @PageableDefault(size = 10, sort = "dateAchat") Pageable pageable,
            Model model) {
        model.addAttribute("achats", achatIntrantService.listerAchatsIntrants(filter, pageable));
        model.addAttribute("achatsAlevin", achatAlevinService.listerAchatsAlevin(filterAlevin, pageable));
        addListAttributes(model);
        return "achat_depense/achats/list";
    }

    @GetMapping("/achats/intrants/new")
    public String formulaireAchatIntrant(Model model) {
        model.addAttribute("achatIntrantForm", new AchatIntrantForm());
        addFormIntrantAttributes(model);
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
            addFormIntrantAttributes(model);
            return "achat_depense/achats/form-intrant";
        }
    }

    @GetMapping("/achats/alevins/new")
    public String formulaireAchatAlevin(Model model) {
        model.addAttribute("achatAlevinForm", new AchatAlevinForm());
        addFormAlevinAttributes(model);
        return "achat_depense/achats/form-alevin";
    }

    @PostMapping("/achats/alevins")
    public String creerAchatAlevin(
            @ModelAttribute("achatAlevinForm") AchatAlevinForm form,
            Model model) {
        try {
            var achat = achatAlevinService.creerAchatAlevin(form);
            return "redirect:/achats/" + achat.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("achatAlevinForm", form);
            addFormAlevinAttributes(model);
            return "achat_depense/achats/form-alevin";
        }
    }

    // @GetMapping("/achats/alevins/{id}/pdf")
    // @ResponseBody
    // public ResponseEntity<byte[]> telechargerPdf(@PathVariable Long id) {
    //     Achat achat = achatAlevinService.getById(id);
        
    //     byte[] pdfContents = achatService.intoPdfAlevin(achat);
        
    //     return ResponseEntity.ok()
    //             .contentType(MediaType.APPLICATION_PDF)
    //             .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"achat-alevin-" + id + ".pdf\"")
    //             .body(pdfContents);
    // }

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

    private void addFormIntrantAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerCategoriesAchatIntrants());
        model.addAttribute("intrants", intrantService.listerActifs());
    }

    private void addFormAlevinAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.trouverParCode("ACHAT_ALEVINS"));
        model.addAttribute("especes", especesService.findAll());
    }

}
