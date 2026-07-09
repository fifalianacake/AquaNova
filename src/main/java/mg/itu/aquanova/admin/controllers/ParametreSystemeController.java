package mg.itu.aquanova.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.admin.models.ParametreSysteme;
import mg.itu.aquanova.admin.models.TypeValeur;
import mg.itu.aquanova.admin.service.ParametreSystemeService;

@Controller
@RequestMapping("/parametres-systeme")
public class ParametreSystemeController {
    private final ParametreSystemeService service;

    public ParametreSystemeController(ParametreSystemeService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("parametres", service.findAll());
        return "admin/parametre-systeme/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("parametre", new ParametreSysteme());
        model.addAttribute("typesValeur", TypeValeur.values());
        return "admin/parametre-systeme/form";
    }

    @PostMapping
    public String save(@ModelAttribute ParametreSysteme parametre, Model model) {
        try {
            service.create(parametre);
            return "redirect:/parametres-systeme";
        } catch (IllegalArgumentException e) {
            model.addAttribute("parametre", parametre);
            model.addAttribute("typesValeur", TypeValeur.values());
            model.addAttribute("error", e.getMessage());
            return "admin/parametre-systeme/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("parametre", service.findById(id));
        return "admin/parametre-systeme/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("parametre", service.findById(id));
        model.addAttribute("typesValeur", TypeValeur.values());
        return "admin/parametre-systeme/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute ParametreSysteme parametre,
            Model model) {

        try {
            service.update(id, parametre);
            return "redirect:/parametres-systeme";
        } catch (IllegalArgumentException e) {
            parametre.setId(id);
            model.addAttribute("parametre", parametre);
            model.addAttribute("typesValeur", TypeValeur.values());
            model.addAttribute("error", e.getMessage());
            return "admin/parametre-systeme/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model) {
        try {
            service.delete(id);
            return "redirect:/parametres-systeme";
        } catch (IllegalArgumentException e) {
            model.addAttribute("parametres", service.findAll());
            model.addAttribute("error", e.getMessage());
            return "admin/parametre-systeme/list";
        }
    }
}
