package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.services.TypeEvenementLotService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("production/types-evenements")
public class TypeEvenementLotController {
    private final TypeEvenementLotService service;

    public TypeEvenementLotController(TypeEvenementLotService service) { this.service = service; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("types", service.listerTous());
        return "production/types-evenements/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("type", new TypeEvenementLot());
        model.addAttribute("libelles", TypeEvenementLot.LibelleEvenement.values());
        return "production/types-evenements/form";
    }

    @PostMapping
    public String create(@ModelAttribute("type") TypeEvenementLot type) {
        service.creer(type);
        return "redirect:/production/types-evenements";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("type", service.trouverParId(id));
        return "production/types-evenements/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("type", service.trouverParId(id));
        model.addAttribute("libelles", TypeEvenementLot.LibelleEvenement.values());
        return "production/types-evenements/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("type") TypeEvenementLot type) {
        service.modifier(id, type);
        return "redirect:/production/types-evenements";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.supprimer(id);
        return "redirect:/production/types-evenements";
    }
}
