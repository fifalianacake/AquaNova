package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.services.MortaliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/production/mortalites")
public class MortaliteController {

    private final MortaliteService mortaliteService;

    MortaliteController(MortaliteService mortaliteService) {
        this.mortaliteService = mortaliteService;
    }

    // 1. Afficher la liste de toutes les mortalités
    @GetMapping
    public String listeMortalites(Model model) {
        model.addAttribute("mortalites", mortaliteService.findAll());
        return "production/mortalites/liste";
    }

    // 2. Afficher le formulaire d'ajout d'une nouvelle mortalité
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("mortalite", new MortaliteModels());
        return "production/mortalites/saisie";
    }

    // 3. Afficher le formulaire de modification d'une mortalité existante
    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        model.addAttribute("mortalite", mortaliteService.findById(id));
        return "production/mortalites/saisie";
    }

    // 4. Traiter l'enregistrement (Ajout ou Modification)
    @PostMapping("/enregistrer")
    public String enregistrerMortalite(@ModelAttribute("mortalite") MortaliteModels mortalite) {
        mortaliteService.save(mortalite);
        return "redirect:/production/mortalites";
    }

    // 5. Supprimer une mortalité
    @GetMapping("/supprimer/{id}")
    public String supprimerMortalite(@PathVariable Integer id) {
        mortaliteService.delete(id);
        return "redirect:/production/mortalites";
    }
}
