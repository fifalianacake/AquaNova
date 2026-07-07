package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.services.JournalLotFilter;
import mg.itu.aquanova.production.services.JournalLotService;
import mg.itu.aquanova.production.services.LotService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class JournalLotController {
    private final JournalLotService journalLotService;
    private final LotService lotService;

    public JournalLotController(JournalLotService journalLotService, LotService lotService) {
        this.journalLotService = journalLotService;
        this.lotService = lotService;
    }

    @GetMapping("/lots/{lotId}/journal")
    public String journalDuLot(@PathVariable Long lotId, Model model) {
        model.addAttribute("lot", lotService.trouverParId(lotId));
        model.addAttribute("journaux", journalLotService.obtenirJournalParLot(lotId));
        return "production/journaux/lot";
    }

    @GetMapping("/journaux-lots")
    public String listeJournaux(
            @RequestParam(required = false) Long lotId,
            @RequestParam(required = false) TypeEvenementLot.LibelleEvenement typeEvenement,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            Model model) {

        JournalLotFilter filter = new JournalLotFilter();
        filter.setLotId(lotId);
        filter.setTypeEvenement(typeEvenement);
        filter.setDateDebut(dateDebut);
        filter.setDateFin(dateFin);

        model.addAttribute("filter", filter);
        model.addAttribute("journaux", journalLotService.rechercher(filter));
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("typesEvenement", TypeEvenementLot.LibelleEvenement.values());
        return "production/journaux/list";
    }
}
