package mg.itu.aquanova.vente.controllers;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.RecolteService;
import mg.itu.aquanova.vente.services.HistoriqueVenteService;

@Controller
@RequestMapping("ventes")
public class HistoriqueVenteController {
    private final HistoriqueVenteService historiqueVenteService;
    private final RecolteService recolteService;
    private final LotService lotService;

    public HistoriqueVenteController(HistoriqueVenteService historiqueVenteService, RecolteService recolteService, LotService lotService) {
        this.historiqueVenteService = historiqueVenteService;
        this.recolteService = recolteService;
        this.lotService = lotService;
    }

    @GetMapping("/historique")
    public String afficherHistorique(@ModelAttribute("filters") mg.itu.aquanova.vente.dto.TransactionFilterDTO filters, Model model) {
        List<mg.itu.aquanova.vente.models.Vente> transaction = historiqueVenteService.searchTransactions(filters);
        model.addAttribute("transactions", transaction);

        model.addAttribute("recoltes", recolteService.getAllRecoltes());
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("statuts", historiqueVenteService.getAllStatuts());


        return "ventes/historique/list";
    }

    @GetMapping("/{id}/journal")
    public String afficherJournal(@PathVariable Long id, Model model) {
        mg.itu.aquanova.vente.models.Vente vente = historiqueVenteService.getJournalByVente(id);
        model.addAttribute("vente", vente);
        return "ventes/historique/journal";
    }
}
