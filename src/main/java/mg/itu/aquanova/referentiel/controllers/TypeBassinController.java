package mg.itu.aquanova.referentiel.controllers;

import mg.itu.aquanova.referentiel.models.TypeBassin;
import mg.itu.aquanova.referentiel.services.BassinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/referentiel/types-bassins")
public class TypeBassinController {

    @Autowired
    private BassinService bassinService;

    // 1. Afficher la liste des types
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("types", bassinService.getAllTypes());
        return "referentiel/types-bassins/liste";
    }

    // 2. Afficher le formulaire d'ajout
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("typeBassin", new TypeBassin());
        return "referentiel/types-bassins/saisie";
    }

    // 3. Afficher le formulaire de modification
    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("typeBassin", bassinService.getTypeBassinById(id));
        return "referentiel/types-bassins/saisie";
    }

    // 4. Traiter l'enregistrement (Ajout ou Modification)
    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute("typeBassin") TypeBassin typeBassin) {
        bassinService.saveTypeBassin(typeBassin);
        return "redirect:/referentiel/types-bassins";
    }

    // 5. Supprimer un type
    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        bassinService.deleteTypeBassin(id);
        return "redirect:/referentiel/types-bassins";
    }
}