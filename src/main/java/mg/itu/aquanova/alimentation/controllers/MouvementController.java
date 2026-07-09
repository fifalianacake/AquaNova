package mg.itu.aquanova.alimentation.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.alimentation.dto.MouvementFilter;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.alimentation.services.MouvementService;
import mg.itu.aquanova.referentiel.services.AlimentService;

@Controller
@RequestMapping("/stocks/mouvements")
public class MouvementController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    @Autowired
    private MouvementService service;

    @Autowired
    private AlimentService alimentService;

    @GetMapping
    public String list(
            @ModelAttribute("filter") MouvementFilter filter,
            @PageableDefault(size = 10, sort = "dateMouvement") Pageable pageable,
            Model model) {

        model.addAttribute("mouvements", service.lister(filter, pageable));
        addListAttributes(model);

        return "alimentation/mouvements/list";
    }

    private void addListAttributes(Model model) {
        model.addAttribute("aliments", alimentService.findAll());
        model.addAttribute("typesMouvement", TypeMouvement.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
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
            model.addAttribute("filter", new MouvementFilter());
            model.addAttribute("mouvements",
                    service.lister(new MouvementFilter(), PageRequest.of(0, 10, Sort.by("dateMouvement"))));
            addListAttributes(model);

            return "alimentation/mouvements/list";
        }
    }

}