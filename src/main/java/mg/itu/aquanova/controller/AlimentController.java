package mg.itu.aquanova.controller;

import mg.itu.aquanova.entity.Aliment;
import mg.itu.aquanova.entity.MouvementStock;
import mg.itu.aquanova.service.AlimentService;
import mg.itu.aquanova.service.MouvementService;
import mg.itu.aquanova.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/aliments")
public class AlimentController {

    private final AlimentService alimentService;
    private final StockService stockService;
    private final MouvementService mouvementService;

    public AlimentController(AlimentService alimentService,
                              StockService stockService,
                              MouvementService mouvementService) {
        this.alimentService = alimentService;
        this.stockService = stockService;
        this.mouvementService = mouvementService;
    }

    @GetMapping
    public String getAll(Model model) {
        List<Aliment> aliments = alimentService.findAll();
        model.addAttribute("aliments", aliments);
        return "aliments/liste";
    }

  
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("aliment", new Aliment());
        return "aliments/saisie";
    }

    @PostMapping
    public String save(
            @RequestParam String nom,
            @RequestParam String type,
            @RequestParam Integer ageMin,
            @RequestParam Integer ageMax,
            @RequestParam(required = false) BigDecimal tailleGranule,
            @RequestParam BigDecimal prixUnitaire,
            @RequestParam BigDecimal seuilAlerteKg,
            RedirectAttributes redirectAttributes) {

        try {
            Aliment aliment = new Aliment(nom, type, ageMin, ageMax, tailleGranule, prixUnitaire, seuilAlerteKg);
            Aliment saved = alimentService.save(aliment);

            redirectAttributes.addFlashAttribute("succes", "Aliment cree avec succes");
            return "redirect:/aliments/" + saved.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la creation: " + e.getMessage());
            return "redirect:/aliments/new";
        }
    }

   
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Aliment aliment = alimentService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + id));

            BigDecimal stockActuel = stockService.getStockByAliment(id);
            List<MouvementStock> mouvementsRecents = mouvementService.getRecentByAliment(id, 10);

            model.addAttribute("aliment", aliment);
            model.addAttribute("stockActuel", stockActuel);
            model.addAttribute("mouvementsRecents", mouvementsRecents);

            return "aliments/fiche";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/aliments";
        }
    }

    /**
     * Page: formulaire de modification, champs prerempli
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Aliment aliment = alimentService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + id));

            model.addAttribute("aliment", aliment);
            return "aliments/saisie";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/aliments";
        }
    }

    /**
     * button modifier -> sauvegarde des modifications
     */
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam String type,
            @RequestParam Integer ageMin,
            @RequestParam Integer ageMax,
            @RequestParam(required = false) BigDecimal tailleGranule,
            @RequestParam BigDecimal prixUnitaire,
            @RequestParam BigDecimal seuilAlerteKg,
            RedirectAttributes redirectAttributes) {

        try {
            alimentService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + id));

            Aliment aliment = new Aliment(nom, type, ageMin, ageMax, tailleGranule, prixUnitaire, seuilAlerteKg);
            aliment.setId(id);
            alimentService.save(aliment);

            redirectAttributes.addFlashAttribute("succes", "Aliment modifie avec succes");
            return "redirect:/aliments/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/aliments/" + id + "/edit";
        }
    }

  
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alimentService.delete(id);
            redirectAttributes.addFlashAttribute("succes", "Aliment supprime avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Impossible de supprimer l'aliment: " + e.getMessage());
        }
        return "redirect:/aliments";
    }

    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleBadPathVariable(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erreur", "URL invalide");
        return "redirect:/aliments";
    }
}