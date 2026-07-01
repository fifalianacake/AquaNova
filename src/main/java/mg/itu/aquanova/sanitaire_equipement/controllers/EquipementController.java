package mg.itu.aquanova.sanitaire_equipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.sanitaire_equipement.models.Equipement;
import mg.itu.aquanova.sanitaire_equipement.services.EquipementService;
import mg.itu.aquanova.sanitaire_equipement.services.TypeEquipementService;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;
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
public String lister(

        @RequestParam(required = false) String nom,

        @RequestParam(required = false) Long type,

        @RequestParam(required = false) StatutEquipement statut,

        @RequestParam(required = false) Long bassin,

        Model model){

    model.addAttribute(
        "equipements",
        equipementService.search(
            nom,
            type,
            statut,
            bassin
        )
    );

    model.addAttribute("types", typeService.listerTous());
    model.addAttribute("statuts", StatutEquipement.values());

    return "equipements/liste";
}

   @GetMapping("/new")
public String formulaire(Model model){

    model.addAttribute("equipement",new Equipement());

    model.addAttribute("types",typeService.listerTous());

    model.addAttribute("statuts", StatutEquipement.values());

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