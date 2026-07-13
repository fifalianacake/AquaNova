package mg.itu.aquanova.production.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.services.JournalLotFilter;
import mg.itu.aquanova.production.services.JournalLotService;
import mg.itu.aquanova.production.services.LotService;

import java.util.List;

@Controller
public class JournalLotController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

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
            @ModelAttribute("filter") JournalLotFilter filter,
            @PageableDefault(size = 10, sort = "dateEvenement") Pageable pageable,
            Model model) {

        model.addAttribute("journaux", journalLotService.lister(filter, pageable));
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("typesEvenement", TypeEvenementLot.LibelleEvenement.values());
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "production/journaux/list";
    }
}
