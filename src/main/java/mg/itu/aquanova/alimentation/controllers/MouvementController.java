package mg.itu.aquanova.alimentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.services.MouvementService;
import mg.itu.aquanova.referentiel.services.AlimentService;

@Controller
@RequestMapping("/stocks/mouvements")
public class MouvementController {

    @Autowired
    private MouvementService service;

    @Autowired
    private AlimentService alimentService;

    @GetMapping
    public String list(Long id,
            String type,
            String aliment,
            String start,
            String end,
            Model model) {

        model.addAttribute("mouvements",
                service.search(id, type, aliment, start, end));

        return "alimentation/mouvements/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
            Model model) {

        model.addAttribute("mouvement",
                service.findById(id));

        // model.addAttribute("lots",
        // mouvementLotRepo.findByMouvementId(id));

        return "alimentation/mouvements/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("mouvement", new MouvementStock());
        model.addAttribute("aliments", alimentService.findAll());

        return "alimentation/mouvements/form";
    }

    @PostMapping
    public String save(@ModelAttribute MouvementStock mouvement, Model model) {

        try {
            service.create(mouvement);
            return "redirect:/stocks/mouvements";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("mouvement", mouvement);
            model.addAttribute("aliments", alimentService.findAll());

            return "alimentation/mouvements/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
            Model model) {

        model.addAttribute("mouvement", service.findById(id));
        model.addAttribute("aliments", alimentService.findAll());

        return "alimentation/mouvements/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute MouvementStock mouvement,
            Model model) {

        try {
            mouvement.setId(id);
            service.update(mouvement);

            return "redirect:/stocks/mouvements";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("mouvement", mouvement);
            model.addAttribute("aliments", alimentService.findAll());

            return "alimentation/mouvements/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
            Model model) {

        try {
            service.delete(id);
            return "redirect:/stocks/mouvements";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("mouvements", service.search(null, null, null, null, null));

            return "alimentation/mouvements/list";
        }
    }

}