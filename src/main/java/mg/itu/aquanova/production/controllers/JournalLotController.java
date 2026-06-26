package mg.itu.aquanova.production.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.services.JournalLotService;
import mg.itu.aquanova.production.services.LotService;

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

    @GetMapping("/journaux")
    public String listeJournaux(Model model) {
        model.addAttribute("journaux", journalLotService.listerTous());
        return "production/journaux/list";
    }
}
