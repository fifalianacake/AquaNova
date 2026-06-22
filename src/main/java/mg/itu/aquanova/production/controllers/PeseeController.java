package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.service.PeseeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/production/pesee")
public class PeseeController {

    @Autowired
    private PeseeService peseeService;

    // F6 - Afficher l'historique des pesees d'un lot
    @GetMapping("/liste/{idLot}")
    public String listerPesees(@PathVariable Long idLot, Model model) {
        model.addAttribute("pesees", peseeService.listerPeseesParLot(idLot));
        model.addAttribute("idLot", idLot);
        return "production/pesee/liste"; 
    }

    // F5 - Page de formulaire d'ajout
    @GetMapping("/ajouter/{idLot}")
    public String formAjouter(@PathVariable Long idLot, Model model) {
        model.addAttribute("idLot", idLot);
        return "production/pesee/formulaire";
    }

    // F5 - Traitement de l'ajout
    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long idLot,
                             @RequestParam LocalDate datePesee,
                             @RequestParam Integer nbEchantillon,
                             @RequestParam BigDecimal poidsTotal,
                             @RequestParam(required = false) String observation) {
        
        peseeService.enregistrerPesee(idLot, datePesee, nbEchantillon, poidsTotal, observation);
        return "redirect:/production/pesee/liste/" + idLot;
    }

    // F6 - Modification : Afficher formulaire
    @GetMapping("/modifier/{id}")
    public String formModifier(@PathVariable Long id, Model model) {
        Pese pese = peseeService.trouverParId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesée invalide"));
        model.addAttribute("pese", pese);
        return "production/pesee/modifier";
    }

    // F6 - Traitement de la modification
    @PostMapping("/modifier")
    public String modifier(@RequestParam Long id,
                           @RequestParam LocalDate datePesee,
                           @RequestParam Integer nbEchantillon,
                           @RequestParam BigDecimal poidsTotal,
                           @RequestParam(required = false) String observation) {
        
        Pese updated = peseeService.modifierPesee(id, datePesee, nbEchantillon, poidsTotal, observation);
        return "redirect:/production/pesee/liste/" + updated.getIdLot();
    }

    // F6 - Suppression
    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        Pese pese = peseeService.trouverParId(id).orElseThrow();
        Long idLot = pese.getIdLot();
        peseeService.supprimerPesee(id);
        return "redirect:/production/pesee/liste/" + idLot;
    }
}
