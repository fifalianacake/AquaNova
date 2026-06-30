package mg.itu.aquanova.stock.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.stock.models.MouvementStock;
import mg.itu.aquanova.stock.repositories.MouvementLotRepository;
import mg.itu.aquanova.stock.services.MouvementService;
import mg.itu.aquanova.stock.services.AlimentService;

@Controller
@RequestMapping("/stocks/mouvements")
public class MouvementController {

    @Autowired
    private MouvementService service;

    @Autowired
    private AlimentService alimentService;

    @Autowired
    private MouvementLotRepository mouvementLotRepo;

    @GetMapping
    public String list(Long id,
            String type,
            String aliment,
            String start,
            String end,
            Model model) {

        model.addAttribute("mouvements",
                service.search(id, type, aliment, start, end));

        return "mouvements/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
            Model model) {

        model.addAttribute("mouvement",
                service.findById(id));

        model.addAttribute("lots",
                mouvementLotRepo.findByMouvementId(id));

        return "mouvements/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute("mouvement", new MouvementStock());
        model.addAttribute("aliments", alimentService.findAll());

        return "mouvements/form";
    }

    @PostMapping
    public String save(@ModelAttribute MouvementStock mouvement) {

        service.create(mouvement);

        return "redirect:/stocks/mouvements";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
            Model model) {

        model.addAttribute("mouvement", service.findById(id));
        model.addAttribute("aliments", alimentService.findAll());

        return "mouvements/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute MouvementStock mouvement) {

        mouvement.setId(id);

        service.update(mouvement);

        return "redirect:/stocks/mouvements";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/stocks/mouvements";
    }

}