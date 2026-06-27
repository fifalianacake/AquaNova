package mg.itu.aquanova.sanitaireequipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import mg.itu.aquanova.sanitaireequipement.models.Equipement;
import mg.itu.aquanova.sanitaireequipement.services.EquipementService;
import mg.itu.aquanova.sanitaireequipement.services.TypeEquipementService;

@Controller
@RequestMapping("/equipements")
public class EquipementController {

    private final EquipementService equipementService;
    private final TypeEquipementService typeService;

    public EquipementController(EquipementService equipementService, TypeEquipementService typeService) {
        this.equipementService = equipementService;
        this.typeService = typeService;
    }

    @GetMapping
    public String lister(@RequestParam(required = false) String nom, Model model) {
        model.addAttribute("equipements", equipementService.search(nom, null, null, null));
        return "equipements/liste";
    }

    @GetMapping("/new")
    public String formulaire(Model model) {
        model.addAttribute("equipement", new Equipement());
        model.addAttribute("types", typeService.listerTous()); // Pour la dropdown
        return "equipements/form";
    }

    @PostMapping("/save")
    public String enregistrer(@ModelAttribute Equipement equipement) {
        equipementService.creer(equipement);
        return "redirect:/equipements";
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("equipement", equipementService.trouverParId(id));
        return "equipements/fiche";
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id) {
        equipementService.supprimer(id);
        return "redirect:/equipements";
    }
}