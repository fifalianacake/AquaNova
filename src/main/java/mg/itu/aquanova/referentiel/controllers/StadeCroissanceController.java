package mg.itu.aquanova.referentiel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
import mg.itu.aquanova.referentiel.services.StadeCroissanceService;

@Controller
@RequestMapping("/stade-croissance")
public class StadeCroissanceController {

    private final StadeCroissanceService service;

    public StadeCroissanceController(StadeCroissanceService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stades", service.findAll());
        return "stade-croissance/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("stade", new StadeCroissanceModels());
        return "stade-croissance/form";
    }

    @PostMapping
    public String save(@ModelAttribute StadeCroissanceModels stade) {
        service.save(stade);
        return "redirect:/stade-croissance";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("stade", service.findById(id));
        return "stade-croissance/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/stade-croissance";
    }
}