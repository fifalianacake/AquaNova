package mg.itu.aquanova.referentiel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.services.EspecesService;

@Controller
@RequestMapping("/especes")
public class EspecesController {

    private final EspecesService service;

    public EspecesController(EspecesService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("especes", service.findAll());
        return "especes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("espece", new EspecesModels());
        return "especes/form";
    }

    @PostMapping
    public String save(@ModelAttribute EspecesModels e, Model model) {
        try {
            service.save(e);
            return "redirect:/especes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("espece", e);
            model.addAttribute("error", ex.getMessage());
            return "especes/form";
        }
    }
    @GetMapping("/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("espece", service.findById(id));
        return "especes/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/especes";
    }
}