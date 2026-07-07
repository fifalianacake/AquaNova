package mg.itu.aquanova.sanitaire_equipement.controllers;

import jakarta.servlet.http.HttpSession;
import mg.itu.aquanova.sanitaire_equipement.models.TypeTraitementEau;
import mg.itu.aquanova.sanitaire_equipement.services.TypeTraitementEauService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/types-traitements-eau")
public class TypeTraitementEauController {

    private final TypeTraitementEauService service;

    public TypeTraitementEauController(TypeTraitementEauService service) {
        this.service = service;
    }

    @GetMapping
    public String lister(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("types", service.listerTous());
        return "sanitaire_equipement/types-traitements-eau/liste";
    }

    @GetMapping("/new")
    public String afficherFormulaireCreation(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("typeTraitement", new TypeTraitementEau());
        return "sanitaire_equipement/types-traitements-eau/formulaire";
    }

    @GetMapping("/{id}/edit")
    public String afficherFormulaireModification(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("typeTraitement", service.trouverParId(id));
        return "sanitaire_equipement/types-traitements-eau/formulaire";
    }

    @GetMapping("/{id}")
    public String afficherFiche(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("typeTraitement", service.trouverParId(id));
        return "sanitaire_equipement/types-traitements-eau/fiche";
    }

    @PostMapping
    public String enregistrer(
            @ModelAttribute("typeTraitement") TypeTraitementEau typeTraitement,
            Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        try {
            service.create(typeTraitement);
            return "redirect:/types-traitements-eau";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("typeTraitement", typeTraitement);
            return "sanitaire_equipement/types-traitements-eau/formulaire";
        }
    }

    @PostMapping("/{id}")
    public String modifier(
            @PathVariable Long id,
            @ModelAttribute("typeTraitement") TypeTraitementEau typeTraitement,
            Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        try {
            service.update(id, typeTraitement);
            return "redirect:/types-traitements-eau";
        } catch (RuntimeException e) {
            typeTraitement.setId(id);
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("typeTraitement", typeTraitement);
            return "sanitaire_equipement/types-traitements-eau/formulaire";
        }
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        service.delete(id);
        return "redirect:/types-traitements-eau";
    }
}
