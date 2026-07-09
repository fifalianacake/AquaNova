package mg.itu.aquanova.referentiel.controllers;

import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.referentiel.services.StatutBassinService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bassins")
public class BassinController {

    private final BassinService bassinService;
    private final StatutBassinService statutBassinService;

    BassinController(BassinService bassinService, StatutBassinService statutBassinService) {
        this.bassinService = bassinService;
        this.statutBassinService = statutBassinService;
    }

    // 1. Afficher la liste de tous les bassins
    @GetMapping
    public String listeBassins(Model model) {
        model.addAttribute("bassins", bassinService.getAllBassins());
        return "referentiel/bassins/liste";
    }

    // 2. Afficher le formulaire d'ajout d'un nouveau bassin
    @GetMapping("/new")
    public String formulaireModification(Model model) {
        model.addAttribute("bassin", new Bassin());
        model.addAttribute("types", bassinService.getAllTypes());
        model.addAttribute("statuts", statutBassinService.findAll());
        return "referentiel/bassins/saisie";
    }

    // 3. Afficher le formulaire de modification d'un bassin existant
    @GetMapping("/{id}/edit")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("bassin", bassinService.getBassinById(id));
        model.addAttribute("types", bassinService.getAllTypes());
        model.addAttribute("statuts", statutBassinService.findAll());
        return "referentiel/bassins/saisie";
    }

    // 4. Traiter l'enregistrement (Ajout ou Modification)
    @PostMapping
    public String enregistrerBassin(@ModelAttribute("bassin") Bassin bassin, Model model) {
        try {
            bassinService.saveBassin(bassin);
            return "redirect:/bassins";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bassin", bassin);
            model.addAttribute("types", bassinService.getAllTypes());
            model.addAttribute("statuts", statutBassinService.findAll());
            return "referentiel/bassins/saisie";
        }
    }

    // 5. Supprimer un bassin
    @GetMapping("/{id}/delete")
    public String supprimerBassin(@PathVariable Long id) {
        bassinService.deleteBassin(id);
        return "redirect:/bassins";
    }
}