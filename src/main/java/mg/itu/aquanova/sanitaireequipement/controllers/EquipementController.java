package mg.itu.aquanova.sanitaireequipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import mg.itu.aquanova.sanitaireequipement.models.Equipement;
import mg.itu.aquanova.sanitaireequipement.services.EquipementService;

@Controller
@RequestMapping("/equipements")
public class EquipementController {

    private final EquipementService service;

    public EquipementController(EquipementService service) {
        this.service = service;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("equipements", service.listerTout());
        return "equipements/liste"; // Chemin de la vue
    }

    @GetMapping("/new")
    public String formSaisie(Model model) {
        model.addAttribute("equipement", new Equipement());
        return "equipements/form";
    }

    @PostMapping("/save")
    public String enregistrer(@ModelAttribute Equipement equipement) {
        service.creer(equipement);
        return "redirect:/equipements";
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return "redirect:/equipements";
    }
}