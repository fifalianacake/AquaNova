package mg.itu.aquanova.finance.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.finance.dto.PrevisionFinanciereDTO;
import mg.itu.aquanova.finance.services.PrevisionFinanciereService;

@Controller
@RequestMapping("/finance")
public class PrevisionFinanciereController {

    private final PrevisionFinanciereService previsionFinanciereService;

    public PrevisionFinanciereController(PrevisionFinanciereService previsionFinanciereService) {
        this.previsionFinanciereService = previsionFinanciereService;
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

        return "finance/prevision/list";
    }

}
