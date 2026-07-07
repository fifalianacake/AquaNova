package mg.itu.aquanova.referentiel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.services.StatutBassinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/statuts-bassins")
public class StatutBassinController {
    private final StatutBassinService service;

    public StatutBassinController(StatutBassinService service){
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("statuts", service.findAll());
        return "referentiel/statuts-bassins/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("statut", new StatutBassin());
        model.addAttribute("libelles", LibelleStatutBassin.values());
        return "referentiel/statuts-bassins/form";
    }

    @PostMapping
    public String save(@ModelAttribute StatutBassin statut) {
        service.save(statut);
        return "redirect:/statuts-bassins";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("statut", service.findById(id));
        return "referentiel/statuts-bassins/details";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("statut", service.findById(id));
        model.addAttribute("libelles", LibelleStatutBassin.values());
        return "referentiel/statuts-bassins/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute StatutBassin statut,
            Model model) {

        try {
            service.update(id, statut);
            return "redirect:/statuts-bassins";
        } catch (IllegalArgumentException e) {
            statut.setId(id);
            model.addAttribute("statut", statut);
            model.addAttribute("libelles", LibelleStatutBassin.values());
            model.addAttribute("error", e.getMessage());
            return "referentiel/statuts-bassins/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/statuts-bassins";
    }
}