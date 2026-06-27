package mg.itu.aquanova.sanitaireequipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import mg.itu.aquanova.sanitaireequipement.models.TypeEquipement;
import mg.itu.aquanova.sanitaireequipement.services.TypeEquipementService;

@Controller
@RequestMapping("/types-equipements")
public class TypeEquipementController {

    private final TypeEquipementService service;

    public TypeEquipementController(TypeEquipementService service) { this.service = service; }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("types", service.listerTous());
        return "types-equipements/liste";
    }

    @PostMapping("/save")
    public String enregistrer(@ModelAttribute TypeEquipement type) {
        service.creer(type);
        return "redirect:/types-equipements";
    }
}