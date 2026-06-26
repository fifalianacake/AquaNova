package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.services.PeseeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/production/pesee")
public class PeseeController {

    private final PeseeService peseeService;

    
    public PeseeController(PeseeService peseeService) {
        this.peseeService = peseeService;
    }

    //  l'HISTORIQUE (F6)
    @GetMapping("/liste/{idLot}")
    public String listerPesees(@PathVariable Long idLot, Model model) {
        
        model.addAttribute("pesees", this.peseeService.listerPeseesParLot(idLot));
        model.addAttribute("idLot", idLot);
        return "production/pesee/liste";
    }

    @GetMapping("/ajouter/{idLot}")
    public String formAjouter(@PathVariable Long idLot, Model model) {
        model.addAttribute("idLot", idLot);
        return "production/pesee/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long idLot,
                             @RequestParam LocalDate datePesee,
                             @RequestParam Integer nbEchantillon,
                             @RequestParam BigDecimal poidsTotal,
                             @RequestParam(required = false) String observation) {
        
        this.peseeService.enregistrerPesee(idLot, datePesee, nbEchantillon, poidsTotal, observation);
        return "redirect:/production/pesee/liste/" + idLot;
    }

    @GetMapping("/modifier/{id}")
    public String formModifier(@PathVariable Long id, Model model) {
        Pese pese = this.peseeService.trouverParId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesée introuvable"));
        model.addAttribute("pese", pese);
        return "production/pesee/modifier";
    }

    @PostMapping("/modifier")
    public String modifier(@RequestParam Long id,
                           @RequestParam LocalDate datePesee,
                           @RequestParam Integer nbEchantillon,
                           @RequestParam BigDecimal poidsTotal,
                           @RequestParam(required = false) String observation) {
        
        Pese updated = this.peseeService.modifierPesee(id, datePesee, nbEchantillon, poidsTotal, observation);
        return "redirect:/production/pesee/liste/" + updated.getIdLot();
    }

    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        Pese pese = this.peseeService.trouverParId(id).orElseThrow();
        Long idLot = pese.getIdLot();
        this.peseeService.supprimerPesee(id);
        return "redirect:/production/pesee/liste/" + idLot;
    }
}
