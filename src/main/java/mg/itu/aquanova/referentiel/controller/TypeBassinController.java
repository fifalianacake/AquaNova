package mg.itu.aquanova.referentiel.controller;

import mg.itu.aquanova.referentiel.model.TypeBassin;
import mg.itu.aquanova.referentiel.service.BassinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/types-bassins")
public class TypeBassinController {

    @Autowired
    private BassinService bassinService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("types", bassinService.getAllTypes());
        return "admin/types-bassins/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("typeBassin", new TypeBassin());
        return "admin/types-bassins/saisie";
    }

    @GetMapping("/modifier/{id}")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("typeBassin", bassinService.getTypeBassinById(id));
        return "admin/types-bassins/saisie";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute("typeBassin") TypeBassin typeBassin) {
        bassinService.saveTypeBassin(typeBassin);
        return "redirect:/admin/types-bassins";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        bassinService.deleteTypeBassin(id);
        return "redirect:/admin/types-bassins";
    }
}