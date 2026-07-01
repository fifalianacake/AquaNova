package mg.itu.aquanova.sanitaire_equipement.controllers;

import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import mg.itu.aquanova.sanitaire_equipement.services.TraitementEauService;
import mg.itu.aquanova.sanitaire_equipement.services.TypeTraitementEauService;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.security.models.UserModels;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
@RequestMapping("/traitements-eau")
public class TraitementEauController {

    private final TraitementEauService service;
    private final TypeTraitementEauService typeService;
    private final BassinService bassinService;

    public TraitementEauController(
            TraitementEauService service,
            TypeTraitementEauService typeService,
            BassinService bassinService) {
        this.service = service;
        this.typeService = typeService;
        this.bassinService = bassinService;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long bassinId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("traitements", service.search(id, bassinId, typeId, debut, fin));
        model.addAttribute("types", typeService.listerTous());
        model.addAttribute("bassins", bassinService.getAllBassins());

        model.addAttribute("currentId", id);
        model.addAttribute("currentBassinId", bassinId);
        model.addAttribute("currentTypeId", typeId);
        model.addAttribute("currentDebut", debut);
        model.addAttribute("currentFin", fin);

        return "sanitaire_equipement/traitements-eau/liste";
    }

    @GetMapping("/new")
    public String afficherFormulaire(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        TraitementEau t = new TraitementEau();
        t.setDateTraitement(LocalDate.now());

        model.addAttribute("traitement", t);
        model.addAttribute("types", typeService.listerTous());
        model.addAttribute("bassins", bassinService.getAllBassins());
        return "sanitaire_equipement/traitements-eau/formulaire";
    }

    @PostMapping
    public String enregistrer(
            @ModelAttribute("traitement") TraitementEau traitement,
            Model model,
            HttpSession session) {
        UserModels user = (UserModels) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            traitement.setUtilisateur(user);
            service.create(traitement);
            return "redirect:/traitements-eau";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("types", typeService.listerTous());
            model.addAttribute("bassins", bassinService.getAllBassins());
            return "sanitaire_equipement/traitements-eau/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String afficherFiche(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("traitement", service.trouverParId(id));
        return "sanitaire_equipement/traitements-eau/fiche";
    }

    @GetMapping("/edit/{id}")
    public String afficherFormulaireModification(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("traitement", service.trouverParId(id));
        model.addAttribute("types", typeService.listerTous());
        model.addAttribute("bassins", bassinService.getAllBassins());
        return "sanitaire_equipement/traitements-eau/formulaire";
    }

    @PostMapping("/edit/{id}")
    public String modifier(
            @PathVariable Long id,
            @ModelAttribute("traitement") TraitementEau traitement,
            Model model,
            HttpSession session) {
        UserModels user = (UserModels) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            traitement.setUtilisateur(user);
            service.update(id, traitement);
            return "redirect:/traitements-eau/" + id;
        } catch (RuntimeException e) {
            traitement.setId(id);
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("traitement", traitement);
            model.addAttribute("types", typeService.listerTous());
            model.addAttribute("bassins", bassinService.getAllBassins());
            return "sanitaire_equipement/traitements-eau/formulaire";
        }
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        service.delete(id);
        return "redirect:/traitements-eau";
    }
}
