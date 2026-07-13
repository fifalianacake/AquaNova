package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.dto.PeseeFilter;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.services.PeseeService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PeseeController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final PeseeService peseeService;


    public PeseeController(PeseeService peseeService) {
        this.peseeService = peseeService;
    }

    //  l'HISTORIQUE (F6)
    @GetMapping("/lots/{idLot}/pesees")
    public String listerPesees(
            @PathVariable Long idLot,
            @ModelAttribute("filter") PeseeFilter filter,
            @PageableDefault(size = 10, sort = "datePesee") Pageable pageable,
            Model model) {

        model.addAttribute("pesees", this.peseeService.lister(idLot, filter, pageable));
        model.addAttribute("idLot", idLot);
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "production/pesee/liste";
    }

    @GetMapping("/lots/{idLot}/pesees/new")
    public String formAjouter(@PathVariable Long idLot, Model model) {
        model.addAttribute("idLot", idLot);
        return "production/pesee/formulaire";
    }

    @PostMapping("/lots/{idLot}/pesees")
    public String enregistrer(@PathVariable Long idLot,
                             @RequestParam LocalDate datePesee,
                             @RequestParam Integer nbEchantillon,
                             @RequestParam Double poidsTotal,
                             @RequestParam(required = false) String observation,
                             Model model) {
        try {
            this.peseeService.enregistrerPesee(idLot, datePesee, nbEchantillon, poidsTotal, observation);
            return "redirect:/lots/" + idLot + "/pesees";
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("idLot", idLot);
            model.addAttribute("datePesee", datePesee);
            model.addAttribute("nbEchantillon", nbEchantillon);
            model.addAttribute("poidsTotal", poidsTotal);
            model.addAttribute("observation", observation);
            return "production/pesee/formulaire";
        }
    }

    @GetMapping("/pesees/{id}/edit")
    public String formModifier(@PathVariable Long id, Model model) {
        Pese pese = this.peseeService.trouverParId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesée introuvable"));
        model.addAttribute("pese", pese);
        return "production/pesee/modifier";
    }

    @PostMapping("/pesees/{id}")
    public String modifier(@PathVariable Long id,
                           @RequestParam LocalDate datePesee,
                           @RequestParam Integer nbEchantillon,
                           @RequestParam Double poidsTotal,
                           @RequestParam(required = false) String observation,
                           Model model) {
        try {
            Pese updated = this.peseeService.modifierPesee(id, datePesee, nbEchantillon, poidsTotal, observation);
            return "redirect:/lots/" + updated.getLot().getId() + "/pesees";
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            Pese pese = this.peseeService.trouverParId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pesée introuvable"));
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pese", pese);
            return "production/pesee/modifier";
        }
    }

    @GetMapping("/pesees/{id}/delete")
    public String supprimer(@PathVariable Long id) {
        Pese pese = this.peseeService.trouverParId(id).orElseThrow();
        Long idLot = pese.getLot().getId();
        this.peseeService.supprimerPesee(id);
        return "redirect:/lots/" + idLot + "/pesees";
    }
}
