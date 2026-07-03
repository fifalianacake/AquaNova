package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.TypeRecolteEnum;
import mg.itu.aquanova.production.models.TypeRecoltes;
import mg.itu.aquanova.production.services.TypeRecolteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/type-recoltes")
public class TypeRecolteController {

    private final TypeRecolteService service;

    public TypeRecolteController(TypeRecolteService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("types", service.getAllTypeRecoltes());
        return "production/type-recoltes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("type", new TypeRecoltes());
        addLibelles(model);
        return "production/type-recoltes/form";
    }

    @PostMapping
    public String create(@ModelAttribute("type") TypeRecoltes type) {
        service.saveTypeRecoltes(type);
        return "redirect:/type-recoltes";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("type", service.getTypeRecolteById(id));
        return "production/type-recoltes/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("type", service.getTypeRecolteById(id));
        addLibelles(model);
        return "production/type-recoltes/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("type") TypeRecoltes type) {
        service.updateTypeRecoltes(id, type);
        return "redirect:/type-recoltes";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.deleteTypeRecoltes(id);
        return "redirect:/type-recoltes";
    }

    private void addLibelles(Model model) {
        model.addAttribute("libelles", TypeRecolteEnum.values());
    }
}
