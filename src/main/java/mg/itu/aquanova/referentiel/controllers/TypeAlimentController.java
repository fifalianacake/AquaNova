package mg.itu.aquanova.referentiel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.referentiel.services.TypeAlimentService;

@Controller
@RequestMapping("/type-aliment")
public class TypeAlimentController {

    private final TypeAlimentService service;

    public TypeAlimentController(TypeAlimentService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("types", service.findAll());
        return "type-aliment/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("type", new TypeAlimentModels());
        return "type-aliment/form";
    }

    @PostMapping
    public String save(@ModelAttribute TypeAlimentModels type, Model model) {
        try {
            service.save(type);
            return "redirect:/type-aliment";
        } catch (IllegalArgumentException e) {
            model.addAttribute("type", type);
            model.addAttribute("error", e.getMessage());
            return "type-aliment/form";
        }
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("type", service.findById(id));
        return "type-aliment/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/type-aliment";
    }
}