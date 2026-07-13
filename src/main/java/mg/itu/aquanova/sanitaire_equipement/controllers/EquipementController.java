package mg.itu.aquanova.sanitaire_equipement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.sanitaire_equipement.models.Equipement;
import mg.itu.aquanova.sanitaire_equipement.services.EquipementService;
import mg.itu.aquanova.sanitaire_equipement.services.TypeEquipementService;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;
import mg.itu.aquanova.referentiel.services.BassinService;

@Controller
@RequestMapping("/equipements")
public class EquipementController {

    private final EquipementService equipementService;
    private final TypeEquipementService typeService;
    private final BassinService bassinService;

    public EquipementController(EquipementService equipementService, TypeEquipementService typeService,
            BassinService bassinService) {
        this.equipementService = equipementService;
        this.typeService = typeService;
        this.bassinService = bassinService;
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
    model.addAttribute("bassins", bassinService.getAllBassins());

    return "sanitaire_equipement/equipements/liste";
}

   @GetMapping("/new")
public String formulaire(Model model){

    model.addAttribute("equipement",new Equipement());

    model.addAttribute("types",typeService.listerTous());

    model.addAttribute("statuts", StatutEquipement.values());

    model.addAttribute("bassins", bassinService.getAllBassins());

    return "sanitaire_equipement/equipements/form";
}

    @PostMapping
    public String enregistrer(@ModelAttribute Equipement equipement) {
        equipementService.creer(equipement);
        return "redirect:/equipements";
    }

    @GetMapping("/{id}/edit")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("equipement", equipementService.trouverParId(id));
        model.addAttribute("types", typeService.listerTous());
        model.addAttribute("statuts", StatutEquipement.values());
        model.addAttribute("bassins", bassinService.getAllBassins());

        return "sanitaire_equipement/equipements/form";
    }

    @PostMapping("/{id}")
    public String modifier(@PathVariable Long id, @ModelAttribute Equipement equipement) {
        equipementService.modifier(id, equipement);
        return "redirect:/equipements/" + id;
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("equipement", equipementService.trouverParId(id));
        return "sanitaire_equipement/equipements/fiche";
    }

    @PostMapping("/{id}/delete")
    public String supprimer(@PathVariable Long id) {
        equipementService.supprimer(id);
        return "redirect:/equipements";
    }
}
