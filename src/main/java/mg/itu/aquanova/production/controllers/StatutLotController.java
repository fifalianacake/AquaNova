package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.services.StatutLotService;

@Controller
public class StatutLotController {

    private final StatutLotService service;

    public StatutLotController(StatutLotService service) {
        this.service = service;
    }

    @GetMapping("/statut-lots")
    public String list(Model model) {
        model.addAttribute("statuts", service.listerTous());
        return "production/statut-lots/list";
    }

    @GetMapping("/statut-lots/new")
    public String createForm(Model model) {
        model.addAttribute("statut", new StatutLotModels());
        model.addAttribute("libelles", StatutLotEnum.values());
        return "production/statut-lots/form";
    }

    @PostMapping("/statut-lots")
    public String save(@ModelAttribute StatutLotModels statut) {
        service.creer(statut);
        return "redirect:/statut-lots";
    }

    @GetMapping("/statut-lots/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("statut", service.trouverParId(id));
        return "production/statut-lots/details";
    }

    @GetMapping("/statut-lots/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("statut", service.trouverParId(id));
        model.addAttribute("libelles", StatutLotEnum.values());
        return "production/statut-lots/form";
    }

    @PostMapping("/statut-lots/{id}")
    public String update(@PathVariable Long id, @ModelAttribute StatutLotModels statut) {
        service.modifier(id, statut);
        return "redirect:/statut-lots";
    }

    @GetMapping("/statut-lots/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.supprimer(id);
        return "redirect:/statut-lots";
    }
}
