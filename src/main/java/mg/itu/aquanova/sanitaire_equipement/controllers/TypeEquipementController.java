package mg.itu.aquanova.sanitaire_equipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.sanitaire_equipement.models.TypeEquipement;
import mg.itu.aquanova.sanitaire_equipement.services.TypeEquipementService;

@Controller
@RequestMapping("/types-equipements")
public class TypeEquipementController {

    private final TypeEquipementService service;

    public TypeEquipementController(TypeEquipementService service) { this.service = service; }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("types", service.listerTous());
        model.addAttribute("typeEquipement", new TypeEquipement());
        return "sanitaire_equipement/equipements/types";
    }

    @PostMapping
    public String enregistrer(@ModelAttribute TypeEquipement type) {
        service.creer(type);
        return "redirect:/types-equipements";
    }
}
