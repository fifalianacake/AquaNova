package mg.itu.aquanova.sanitaire_equipement.controllers;

import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import mg.itu.aquanova.sanitaire_equipement.services.TraitementEauService;
import mg.itu.aquanova.sanitaire_equipement.services.TypeTraitementEauService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/traitements-eau")
public class TraitementEauController {

    private final TraitementEauService service;
    private final TypeTraitementEauService typeService;

    public TraitementEauController(TraitementEauService service, TypeTraitementEauService typeService) {
        this.service = service;
        this.typeService = typeService;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long bassinId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {
        
        model.addAttribute("traitements", service.search(id, bassinId, typeId, debut, fin));
        model.addAttribute("types", typeService.listerTous());

        model.addAttribute("currentId", id);
        model.addAttribute("currentBassinId", bassinId);
        model.addAttribute("currentTypeId", typeId);
        model.addAttribute("currentDebut", debut);
        model.addAttribute("currentFin", fin);

        return "traitements-eau/liste";
    }

    @GetMapping("/new")
    public String afficherFormulaire(Model model) {
        TraitementEau t = new TraitementEau();
        t.setDateTraitement(LocalDate.now());

        model.addAttribute("traitement", t);
        model.addAttribute("types", typeService.listerTous());
        return "traitements-eau/formulaire";
    }

    @PostMapping
    public String enregistrer(@ModelAttribute("traitement") TraitementEau traitement, Model model) {
        try {
            service.create(traitement);
            return "redirect:/traitements-eau";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("types", typeService.listerTous());
            return "traitements-eau/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String afficherFiche(@PathVariable Long id, Model model) {
        model.addAttribute("traitement", service.trouverParId(id));
        return "traitements-eau/fiche";
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/traitements-eau";
    }
}