package mg.itu.aquanova.referentiel.controller;

import mg.itu.aquanova.referentiel.model.Bassin;
import mg.itu.aquanova.referentiel.model.StatutBassin;
import mg.itu.aquanova.referentiel.service.BassinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/bassins")
public class BassinController {

    @Autowired
    private BassinService bassinService;

    // 1. Afficher la liste de tous les bassins
    @GetMapping
    public String listeBassins(Model model) {
        model.addAttribute("bassins", bassinService.getAllBassins());
        return "admin/bassins/liste";
    }

    // 2. Afficher le formulaire d'ajout d'un nouveau bassin
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("bassin", new Bassin());
        model.addAttribute("types", bassinService.getAllTypes());
        model.addAttribute("statuts", StatutBassin.values()); // Envoie l'ENUM (Libre, Occupe, Maintenance)
        return "admin/bassins/saisie";
    }

    // 3. Afficher le formulaire de modification d'un bassin existant
    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("bassin", bassinService.getBassinById(id));
        model.addAttribute("types", bassinService.getAllTypes());
        model.addAttribute("statuts", StatutBassin.values());
        return "admin/bassins/saisie";
    }

    // 4. Traiter l'enregistrement (Ajout ou Modification)
    @PostMapping("/enregistrer")
    public String enregistrerBassin(@ModelAttribute("bassin") Bassin bassin) {
        bassinService.saveBassin(bassin);
        return "redirect:/admin/bassins";
    }

    // 5. Supprimer un bassin
    @GetMapping("/supprimer/{id}")
    public String supprimerBassin(@PathVariable Long id) {
        bassinService.deleteBassin(id);
        return "redirect:/admin/bassins";
    }
}