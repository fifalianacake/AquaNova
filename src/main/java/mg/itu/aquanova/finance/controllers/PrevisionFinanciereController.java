package mg.itu.aquanova.finance.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.finance.dto.PrevisionFinanciereDTO;
import mg.itu.aquanova.finance.services.PrevisionFinanciereService;
import mg.itu.aquanova.finance.services.SimulationRecolteService;

@Controller
@RequestMapping("/finance")
public class PrevisionFinanciereController {

    private final PrevisionFinanciereService previsionFinanciereService;
    private final SimulationRecolteService simulationRecolteService;

    public PrevisionFinanciereController(PrevisionFinanciereService previsionFinanciereService,
                                         SimulationRecolteService simulationRecolteService) {
        this.previsionFinanciereService = previsionFinanciereService;
        this.simulationRecolteService = simulationRecolteService;
    }

    /**
     * Simulation de la date de récolte optimale d'un lot.
     *
     * Les hypothèses (prix de vente, coût de l'aliment, mortalité) sont passées en paramètres :
     * l'utilisateur peut les faire varier pour voir la courbe se déformer. Rien n'est écrit en
     * base — c'est une aide à la décision, pas un enregistrement.
     */
    @GetMapping("/lots/{id}/simulation")
    public String simulerRecolte(@PathVariable Long id,
                                 @RequestParam(required = false) Double prixVenteKg,
                                 @RequestParam(required = false) Double coutAlimentKg,
                                 @RequestParam(required = false) Double tauxMortalite,
                                 @RequestParam(required = false) Integer horizonJours,
                                 Model model) {

        model.addAttribute("simulation", simulationRecolteService.simuler(
                id, prixVenteKg, coutAlimentKg, tauxMortalite, horizonJours));
        return "finance/prevision/simulation";
    }

    @GetMapping("/previsions")
    public String afficherPrevisions(
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            Model model) {

        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
        if (dateFin == null) {
            dateFin = dateDebut.plusMonths(6);
        }

        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        if (dateFin.isBefore(dateDebut)) {
            model.addAttribute("erreur", "La date de fin doit être postérieure ou égale à la date de début.");
            model.addAttribute("previsions", List.<PrevisionFinanciereDTO>of());
            return "finance/prevision/list";
        }

        List<PrevisionFinanciereDTO> previsions = previsionFinanciereService.genererPrevisions(dateDebut, dateFin);
        model.addAttribute("previsions", previsions);

        model.addAttribute("totalBiomasse", somme(previsions, PrevisionFinanciereDTO::getBiomassePrevue));
        model.addAttribute("totalCa", somme(previsions, PrevisionFinanciereDTO::getCaPrevisionnel));
        model.addAttribute("totalProfit", somme(previsions, PrevisionFinanciereDTO::getProfitPrevisionnel));

        return "finance/prevision/list";
    }

    private double somme(List<PrevisionFinanciereDTO> previsions,
                         java.util.function.ToDoubleFunction<PrevisionFinanciereDTO> extracteur) {
        return previsions.stream().mapToDouble(extracteur).sum();
    }
}
