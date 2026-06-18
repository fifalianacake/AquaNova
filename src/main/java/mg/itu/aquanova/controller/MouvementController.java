package mg.itu.aquanova.controller;

import mg.itu.aquanova.entity.Aliment;
import mg.itu.aquanova.entity.MouvementStock;
import mg.itu.aquanova.entity.TypeMouvement;
import mg.itu.aquanova.exception.StockInsuffisantException;
import mg.itu.aquanova.service.AlimentService;
import mg.itu.aquanova.service.MouvementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/stocks/mouvements")
public class MouvementController {

    private final MouvementService mouvementService;
    private final AlimentService alimentService;

    public MouvementController(MouvementService mouvementService, AlimentService alimentService) {
        this.mouvementService = mouvementService;
        this.alimentService = alimentService;
    }

    /**
     * Page: Liste mouvements /stocks/mouvements
     * Recherche multi-criteres: id, date range, aliment, type
     */
    @GetMapping
    public String search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long alimentId,
            @RequestParam(required = false) TypeMouvement type,
            Model model) {

        List<MouvementStock> mouvements = mouvementService.search(id, dateDebut, dateFin, alimentId, type);

        model.addAttribute("mouvements", mouvements);
        model.addAttribute("aliments", alimentService.findAll());

        // re-affichage des criteres dans le formulaire de recherche
        model.addAttribute("id", id);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        model.addAttribute("alimentId", alimentId);
        model.addAttribute("type", type);

        return "mouvements/liste";
    }

    /**
     * Page: Fiche mouvement /stocks/mouvements/{id}
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MouvementStock mouvement = mouvementService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mouvement introuvable avec id=" + id));

            model.addAttribute("mouvement", mouvement);
            return "mouvements/fiche";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/stocks/mouvements";
        }
    }

    /**
     * Page: Saisie mouvement /stocks/mouvements/new
     * Formulaire: date, aliment (dropdown), type (dropdown), montant, commentaire
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("aliments", alimentService.findAll());
        model.addAttribute("types", TypeMouvement.values());
        return "mouvements/saisie";
    }

    /**
     * @PostMapping("/stocks/mouvements")
     * Cree le mouvement. Validation (montant > 0, stock suffisant si SORTIE) faite dans MouvementService.create.
     */
    @PostMapping
    public String create(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long alimentId,
            @RequestParam TypeMouvement type,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) String commentaire,
            RedirectAttributes redirectAttributes) {

        try {
            Aliment aliment = alimentService.findById(alimentId)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + alimentId));

            MouvementStock mouvement = new MouvementStock(date, aliment, type, montant, commentaire);
            mouvementService.create(mouvement);

            redirectAttributes.addFlashAttribute("succes", "Mouvement enregistre avec succes");
            return "redirect:/stocks/mouvements";

        } catch (StockInsuffisantException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/stocks/mouvements/new";
        }
    }

    /**
     * Page: formulaire de modification, champs prerempli
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MouvementStock mouvement = mouvementService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mouvement introuvable avec id=" + id));

            model.addAttribute("mouvement", mouvement);
            model.addAttribute("aliments", alimentService.findAll());
            model.addAttribute("types", TypeMouvement.values());
            return "mouvements/saisie";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/stocks/mouvements";
        }
    }

    /**
     * button modifier -> sauvegarde des modifications
     */
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long alimentId,
            @RequestParam TypeMouvement type,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) String commentaire,
            RedirectAttributes redirectAttributes) {

        try {
            mouvementService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mouvement introuvable avec id=" + id));

            Aliment aliment = alimentService.findById(alimentId)
                    .orElseThrow(() -> new IllegalArgumentException("Aliment introuvable avec id=" + alimentId));

            MouvementStock mouvement = new MouvementStock(date, aliment, type, montant, commentaire);
            mouvement.setId(id);
            mouvementService.create(mouvement);

            redirectAttributes.addFlashAttribute("succes", "Mouvement modifie avec succes");
            return "redirect:/stocks/mouvements/" + id;

        } catch (StockInsuffisantException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/stocks/mouvements/" + id + "/edit";
        }
    }

    /**
     * button supprimer -> DELETE /stocks/mouvements/{id}
     * Formulaire HTML standard ne supportant pas DELETE, on expose un POST /delete.
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mouvementService.delete(id);
            redirectAttributes.addFlashAttribute("succes", "Mouvement supprime avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Impossible de supprimer le mouvement: " + e.getMessage());
        }
        return "redirect:/stocks/mouvements";
    }
}