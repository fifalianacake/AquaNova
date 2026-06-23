package mg.itu.aquanova.production.controllers;

import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.services.JournalLotService;
import java.util.List;

@RestController
@RequestMapping("/api/production/journaux")
public class JournalLotController {
    private final JournalLotService journalLotService;

    public JournalLotController(JournalLotService journalLotService) { this.journalLotService = journalLotService; }

    @GetMapping
    public List<JournalLot> getAllLogs() { return journalLotService.listerTous(); }

    @GetMapping("/lot/{lotId}")
    public List<JournalLot> getLogByLot(@PathVariable Long lotId) { return journalLotService.obtenirJournalParLot(lotId); }

    @DeleteMapping("/{id}")
    public void deleteLog(@PathVariable Long id) { journalLotService.supprimer(id); }
}