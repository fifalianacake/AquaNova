package mg.itu.aquanova.production.controllers;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.MortaliteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MortaliteController {

    private final MortaliteService mortaliteService;
    private final LotService lotService;

    MortaliteController(MortaliteService mortaliteService, LotService lotService) {
        this.mortaliteService = mortaliteService;
        this.lotService = lotService;
    }

    // 1. Afficher la liste de toutes les mortalités
    @GetMapping("/mortalites")
    public String listeMortalites(Model model) {
        model.addAttribute("mortalites", mortaliteService.findAll());
        model.addAttribute("titre", "Suivi des Mortalités");
        return "production/mortalites/liste";
    }

    @GetMapping("/lots/{lotId}/mortalites")
    public String listeMortalitesParLot(@PathVariable Long lotId, Model model) {
        LotModels lot = lotService.trouverParId(lotId);
        model.addAttribute("lot", lot);
        model.addAttribute("mortalites", mortaliteService.findByLot(lotId));
        model.addAttribute("titre", "Historique des mortalités du lot " + lot.getCode());
        return "production/mortalites/liste";
    }

    // 2. Afficher le formulaire d'ajout d'une nouvelle mortalité
    @GetMapping("/mortalites/new")
    public String formulaireCreation(Model model) {
        model.addAttribute("mortalite", new MortaliteModels());
        addFormLists(model);
        return "production/mortalites/saisie";
    }

    @GetMapping("/lots/{lotId}/mortalites/new")
    public String formulaireCreationPourLot(@PathVariable Long lotId, Model model) {
        MortaliteModels mortalite = new MortaliteModels();
        mortalite.setLot(lotService.trouverParId(lotId));
        model.addAttribute("mortalite", mortalite);
        addFormLists(model);
        return "production/mortalites/saisie";
    }

    // 3. Afficher le formulaire de modification d'une mortalité existante
    @GetMapping("/mortalites/{id}/edit")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        model.addAttribute("mortalite", mortaliteService.findById(id));
        addFormLists(model);
        return "production/mortalites/saisie";
    }

    // 4. Traiter l'enregistrement (Ajout ou Modification)
    @PostMapping("/mortalites")
    public String enregistrerMortalite(@ModelAttribute("mortalite") MortaliteModels mortalite, Model model) {
        try {
            MortaliteModels saved = mortaliteService.save(mortalite);
            return "redirect:/lots/" + saved.getLot().getId() + "/mortalites";
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("mortalite", mortalite);
            addFormLists(model);
            return "production/mortalites/saisie";
        }
    }

    private void addFormLists(Model model) {
        model.addAttribute("lots", lotService.listerTous());
    }
}
