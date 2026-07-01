package mg.itu.aquanova.alimentation.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.alimentation.models.Aliment;
import mg.itu.aquanova.alimentation.services.AlimentService;

@Controller
@RequestMapping("/aliments")
public class AlimentController {

    private final AlimentService service;

    public AlimentController(AlimentService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("aliments", service.searchByNom(q));
        model.addAttribute("q", q);
        return "alimentation/aliments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("aliment", new Aliment());
        return "alimentation/aliments/form";
    }

    @PostMapping
    public String save(@ModelAttribute Aliment aliment) {
        service.save(aliment);
        return "redirect:/aliments";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Integer id, Model model) {
        Aliment aliment = service.findById(id);
        model.addAttribute("aliment", aliment);
        model.addAttribute("stockActuel", service.getStockActuel(id));
        return "alimentation/aliments/details";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("aliment", service.findById(id));
        return "alimentation/aliments/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/aliments";
    }
}