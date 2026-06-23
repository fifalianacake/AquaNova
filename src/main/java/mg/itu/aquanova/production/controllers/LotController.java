package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.LotFilter;
import mg.itu.aquanova.referentiel.services.EspecesService;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.referentiel.services.StadeCroissanceService;
import mg.itu.aquanova.production.services.StatutLotService;

@Controller
public class LotController {

    private final LotService service;
    private final EspecesService especesService;
    private final BassinService bassinService;
    private final StadeCroissanceService stadeService;
    private final StatutLotService statutService;

    public LotController(LotService service, EspecesService especesService, BassinService bassinService, StadeCroissanceService stadeService, StatutLotService statutService) {
        this.service = service;
        this.especesService = especesService;
        this.bassinService = bassinService;
        this.stadeService = stadeService;
        this.statutService = statutService;
    }

    @GetMapping("/lots")
    public String list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer especeId,
            @RequestParam(required = false) Long bassinId,
            @RequestParam(required = false) Integer stadeId,
            @RequestParam(required = false) Long statutId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer effectifMin,
            @RequestParam(required = false) Integer effectifMax,
            Model model
    ) {
        LotFilter filter = new LotFilter();
        filter.setId(id);
        filter.setCode(code);
        filter.setEspeceId(especeId);
        filter.setBassinId(bassinId);
        filter.setStadeId(stadeId);
        filter.setStatutId(statutId);
        filter.setDateFrom(dateFrom);
        filter.setDateTo(dateTo);
        filter.setEffectifMin(effectifMin);
        filter.setEffectifMax(effectifMax);

        model.addAttribute("lots", service.lister(filter));
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("stades", stadeService.findAll());
        model.addAttribute("statuts", statutService.listerTous());
        return "production/lots/list";
    }

    @GetMapping("/lots/new")
    public String createForm(Model model) {
        model.addAttribute("lot", new LotModels());
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("stades", stadeService.findAll());
        model.addAttribute("statuts", statutService.listerTous());
        return "production/lots/form";
    }

    @PostMapping("/lots")
    public String save(@ModelAttribute LotModels lot) {
        service.creer(lot);
        return "redirect:/lots";
    }

    @GetMapping("/lots/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("lot", service.trouverParId(id));
        return "production/lots/details";
    }

    @GetMapping("/lots/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("lot", service.trouverParId(id));
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("stades", stadeService.findAll());
        model.addAttribute("statuts", statutService.listerTous());
        return "production/lots/form";
    }

    @PostMapping("/lots/{id}")
    public String update(@PathVariable Long id, @ModelAttribute LotModels lot) {
        service.modifier(id, lot);
        return "redirect:/lots";
    }

    @GetMapping("/lots/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.supprimer(id);
        return "redirect:/lots";
    }
}
