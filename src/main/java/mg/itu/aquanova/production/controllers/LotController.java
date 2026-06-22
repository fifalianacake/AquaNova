package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.services.LotService;
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
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String code,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer especeId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long bassinId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer stadeId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long statutId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String dateFrom,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String dateTo,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer effectifMin,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer effectifMax,
            Model model
    ) {
        java.util.List<LotModels> lots = service.listerTous();

        java.util.stream.Stream<LotModels> stream = lots.stream();

        if (id != null) {
            stream = stream.filter(l -> l.getId() != null && l.getId().equals(id));
        }
        if (code != null && !code.isBlank()) {
            String lower = code.toLowerCase();
            stream = stream.filter(l -> l.getCode() != null && l.getCode().toLowerCase().contains(lower));
        }
        if (especeId != null) {
            stream = stream.filter(l -> l.getEspece() != null && l.getEspece().getId() != null && l.getEspece().getId().equals(especeId));
        }
        if (bassinId != null) {
            stream = stream.filter(l -> l.getBassin() != null && l.getBassin().getId() != null && l.getBassin().getId().equals(bassinId));
        }
        if (stadeId != null) {
            stream = stream.filter(l -> l.getStadeCroissance() != null && l.getStadeCroissance().getId() != null && l.getStadeCroissance().getId().equals(stadeId));
        }
        if (statutId != null) {
            stream = stream.filter(l -> l.getStatutLot() != null && l.getStatutLot().getId() != null && l.getStatutLot().getId().equals(statutId));
        }

        java.time.LocalDate fromDate = null;
        java.time.LocalDate toDate = null;
        try {
            if (dateFrom != null && !dateFrom.isBlank()) fromDate = java.time.LocalDate.parse(dateFrom);
        } catch (java.time.format.DateTimeParseException ex) {
            fromDate = null;
        }
        try {
            if (dateTo != null && !dateTo.isBlank()) toDate = java.time.LocalDate.parse(dateTo);
        } catch (java.time.format.DateTimeParseException ex) {
            toDate = null;
        }
        if (fromDate != null) {
            java.time.LocalDate fd = fromDate;
            stream = stream.filter(l -> l.getDateMiseEnCharge() != null && !l.getDateMiseEnCharge().isBefore(fd));
        }
        if (toDate != null) {
            java.time.LocalDate td = toDate;
            stream = stream.filter(l -> l.getDateMiseEnCharge() != null && !l.getDateMiseEnCharge().isAfter(td));
        }
        if (effectifMin != null) {
            stream = stream.filter(l -> l.getEffectifActuel() != null && l.getEffectifActuel() >= effectifMin);
        }
        if (effectifMax != null) {
            stream = stream.filter(l -> l.getEffectifActuel() != null && l.getEffectifActuel() <= effectifMax);
        }

        java.util.List<LotModels> filtered = stream.toList();

        model.addAttribute("lots", filtered);
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
