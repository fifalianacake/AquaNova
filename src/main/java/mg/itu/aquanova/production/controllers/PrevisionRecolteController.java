package mg.itu.aquanova.production.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.PrevisionRecolteFilter;
import mg.itu.aquanova.production.services.PrevisionRecolteService;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.referentiel.services.EspecesService;

@Controller
public class PrevisionRecolteController {
    private final PrevisionRecolteService previsionRecolteService;
    private final LotService lotService;
    private final EspecesService especesService;
    private final BassinService bassinService;

    public PrevisionRecolteController(
            PrevisionRecolteService previsionRecolteService,
            LotService lotService,
            EspecesService especesService,
            BassinService bassinService) {
        this.previsionRecolteService = previsionRecolteService;
        this.lotService = lotService;
        this.especesService = especesService;
        this.bassinService = bassinService;
    }

    @GetMapping("/production/previsions-recoltes")
    public String list(
            @RequestParam(required = false) Long lotId,
            @RequestParam(required = false) Integer especeId,
            @RequestParam(required = false) Long bassinId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {

        PrevisionRecolteFilter filter = new PrevisionRecolteFilter();
        filter.setLotId(lotId);
        filter.setEspeceId(especeId);
        filter.setBassinId(bassinId);
        filter.setDateDebut(dateDebut);
        filter.setDateFin(dateFin);

        model.addAttribute("filter", filter);
        model.addAttribute("previsions", previsionRecolteService.rechercher(filter));
        model.addAttribute("lotsProchesRecolte", previsionRecolteService.getLotsProchesRecolte(filter));
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("especes", especesService.findAll());
        model.addAttribute("bassins", bassinService.getAllBassins());
        return "production/previsions-recoltes/list";
    }
}
