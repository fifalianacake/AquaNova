package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.services.LotService;

@Controller
public class LotController {

    private final LotService service;

    public LotController(LotService service) {
        this.service = service;
    }

    @GetMapping("/lots")
    public String list(Model model) {
        model.addAttribute("lots", service.listerTous());
        return "production/lots/list";
    }

    @GetMapping("/lots/new")
    public String createForm(Model model) {
        model.addAttribute("lot", new LotModels());
        return "production/lots/form";
    }

    @PostMapping("/lots")
    public String save(@ModelAttribute LotModels lot) {
        service.creer(lot);
        return "redirect:/lots";
    }

    @GetMapping("/lots/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("lot", service.trouverParId(id));
        return "production/lots/details";
    }

    @GetMapping("/lots/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("lot", service.trouverParId(id));
        return "production/lots/form";
    }

    @PostMapping("/lots/{id}")
    public String update(@PathVariable Long id, @ModelAttribute LotModels lot) {
        service.modifier(id, lot);
        return "redirect:/lots";
    }

    @GetMapping("/lots/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.supprimer(id);
        return "redirect:/lots";
    }
}
