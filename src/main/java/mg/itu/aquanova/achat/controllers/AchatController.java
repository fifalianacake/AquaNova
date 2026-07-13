package mg.itu.aquanova.achat.controllers;

import java.util.Collections;
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

import mg.itu.aquanova.achat.dto.AchatAlevinFilter;
import mg.itu.aquanova.achat.dto.AchatAlevinForm;
import mg.itu.aquanova.achat.dto.AchatProvendeFilter;
import mg.itu.aquanova.achat.dto.AchatProvendeForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.services.AchatAlevinService;
import mg.itu.aquanova.achat.services.AchatProvendeService;
import mg.itu.aquanova.achat.services.AchatService;
import mg.itu.aquanova.achat.services.CategorieDepenseService;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.referentiel.services.AlimentService;
import mg.itu.aquanova.referentiel.services.EspecesService;

@Controller
public class AchatController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final AchatService achatService;
    private final AchatAlevinService achatAlevinService;
    private final AchatProvendeService achatProvendeService;
    private final FournisseurService fournisseurService;
    private final CategorieDepenseService categorieDepenseService;
    private final EspecesService especesService;
    private final AlimentService alimentService;

    public AchatController(
            AchatService achatService,
            AchatAlevinService achatAlevinService,
            AchatProvendeService achatProvendeService,
            FournisseurService fournisseurService,
            CategorieDepenseService categorieDepenseService,
            EspecesService especesService,
            AlimentService alimentService) {
        this.achatService = achatService;
        this.achatAlevinService = achatAlevinService;
        this.achatProvendeService = achatProvendeService;
        this.fournisseurService = fournisseurService;
        this.categorieDepenseService = categorieDepenseService;
        this.especesService = especesService;
        this.alimentService = alimentService;
    }

    @GetMapping("/achats")
    public String liste() {
        return "redirect:/achats/provende";
    }

    @GetMapping("/achats/alevins")
    public String listeAlevins(
            @ModelAttribute("filter") AchatAlevinFilter filter,
            @PageableDefault(size = 10, sort = "dateAchat") Pageable pageable,
            Model model) {
        model.addAttribute("achats", achatAlevinService.listerAchatsAlevin(filter, pageable));
        addListAlevinAttributes(model);
        return "achat_depense/achats/liste-alevins";
    }

    @GetMapping("/achats/provende")
    public String listeProvende(
            @ModelAttribute("filter") AchatProvendeFilter filter,
            @PageableDefault(size = 10, sort = "dateAchat") Pageable pageable,
            Model model) {
        model.addAttribute("achats", achatProvendeService.listerAchatsProvende(filter, pageable));
        addListProvendeAttributes(model);
        return "achat_depense/achats/liste-provende";
    }

    @GetMapping("/achats/alevins/new")
    public String formulaireAchatAlevin(Model model) {
        AchatAlevinForm form = new AchatAlevinForm();
        form.setCategorieDepenseId(categorieDepenseService.trouverParCode("ACHAT_ALEVINS").getId());
        model.addAttribute("achatAlevinForm", form);
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

    @GetMapping("/achats/provende/new")
    public String formulaireAchatProvende(Model model) {
        AchatProvendeForm form = new AchatProvendeForm();
        try {
            form.setCategorieDepenseId(categorieDepenseService.trouverParCode("ACHAT_PROVENDE").getId());
        } catch (Exception ignored) {
            // Aucune catégorie ACHAT_PROVENDE configurée : le formulaire l'affichera comme non disponible.
        }
        model.addAttribute("achatProvendeForm", form);
        addFormProvendeAttributes(model);
        return "achat_depense/achats/form-provende";
    }

    @PostMapping("/achats/provende")
    public String creerAchatProvende(
            @ModelAttribute("achatProvendeForm") AchatProvendeForm form,
            Model model) {
        try {
            var achat = achatProvendeService.createAchatProvende(form);
            return "redirect:/achats/" + achat.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("achatProvendeForm", form);
            addFormProvendeAttributes(model);
            return "achat_depense/achats/form-provende";
        }
    }

    @GetMapping("/achats/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("achat", achatService.trouverParId(id));
        return "achat_depense/achats/detail";
    }

    @PostMapping("/achats/{id}/valider")
    public String valider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Achat achat = achatService.trouverParId(id);
            CategorieDepense categorie = achat.getCategorieDepense();
            if (categorieDepenseService.estCategorieAchatAlevin(categorie)) {
                achatAlevinService.validerAchat(id);
                redirectAttributes.addFlashAttribute("success", "Achat validé et lot créé.");
            } else if (categorie != null && "ACHAT_PROVENDE".equalsIgnoreCase(categorie.getCode())) {
                achatProvendeService.validerAchat(id);
                redirectAttributes.addFlashAttribute("success", "Achat validé et stock alimenté.");
            } else {
                throw new IllegalStateException("Type d'achat non reconnu : validation impossible.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/achats/" + id;
    }

    @PostMapping("/achats/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Achat achat = achatService.trouverParId(id);
            CategorieDepense categorie = achat.getCategorieDepense();
            if (categorieDepenseService.estCategorieAchatAlevin(categorie)) {
                achatAlevinService.annulerAchat(id);
            } else if (categorie != null && "ACHAT_PROVENDE".equalsIgnoreCase(categorie.getCode())) {
                achatProvendeService.annulerAchat(id);
            } else {
                throw new IllegalStateException("Type d'achat non reconnu : annulation impossible.");
            }
            redirectAttributes.addFlashAttribute("success", "Achat annulé.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/achats/" + id;
    }

    private void addListAlevinAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerTous());
        model.addAttribute("categoriesDepense", categorieDepenseService.trouverParCode("ACHAT_ALEVINS"));
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("statutsAchat", StatutAchat.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }

    private void addListProvendeAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerTous());
        try {
            model.addAttribute("categoriesDepense", Collections.singletonList(categorieDepenseService.trouverParCode("ACHAT_PROVENDE")));
        } catch (Exception e) {
            model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
        }
        model.addAttribute("aliments", alimentService.findAll());
        model.addAttribute("statutsAchat", StatutAchat.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }

    private void addFormAlevinAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        model.addAttribute("categoriesDepense", categorieDepenseService.trouverParCode("ACHAT_ALEVINS"));
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("bassins", achatAlevinService.listerBassinsLibres());
    }

    private void addFormProvendeAttributes(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.listerActifs());
        try {
            model.addAttribute("categoriesDepense", Collections.singletonList(categorieDepenseService.trouverParCode("ACHAT_PROVENDE")));
        } catch (Exception e) {
            model.addAttribute("categoriesDepense", categorieDepenseService.listerTous());
        }
        model.addAttribute("aliments", alimentService.findAll());
    }

}
