package mg.itu.aquanova.alerte.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.alerte.services.AlerteService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;

/**
 * Contrôleur web pour l'historique des alertes.
 * Lecture seule : aucune suppression n'est exposée (règle métier).
 */
@Controller
@RequestMapping("/alertes")
public class AlerteController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final AlerteService alerteService;
    private final LotRepository lotRepository;
    private final BassinsRepository bassinsRepository;

    public AlerteController(AlerteService alerteService,
                            LotRepository lotRepository,
                            BassinsRepository bassinsRepository) {
        this.alerteService = alerteService;
        this.lotRepository = lotRepository;
        this.bassinsRepository = bassinsRepository;
    }

    /**
     * Page HTML : historique des alertes avec filtres et pagination.
     */
    @GetMapping("/historique")
    public String historique(
            @ModelAttribute("filter") AlerteFilterDTO filter,
            @PageableDefault(size = 10, sort = "dateCreation") Pageable pageable,
            Model model) {

        model.addAttribute("alertes", alerteService.searchHistorique(filter, pageable));
        addFilterAttributes(model);
        return "alertes/historique";
    }

    /**
     * Export PDF de l'historique filtré.
     */
    @GetMapping("/historique/export/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf(@ModelAttribute AlerteFilterDTO filter) {
        byte[] pdf = alerteService.exportHistoriquePdf(filter);
        return PdfResponses.attachment(pdf, "historique-alertes.pdf");
    }

    // ── Attributs de formulaire pour les filtres ──

    private void addFilterAttributes(Model model) {
        model.addAttribute("moduleSources", ModuleSource.values());
        model.addAttribute("typesAlerte", TypeAlerte.values());
        model.addAttribute("niveauxCriticite", NiveauCriticite.values());
        model.addAttribute("statutsAlerte", StatutAlerte.values());
        model.addAttribute("lots", lotRepository.findAll());
        model.addAttribute("bassins", bassinsRepository.findAll());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }
    
    @PostMapping("/{id}/statut")
    public String changerStatut(
            @PathVariable Long id,
            UpdateStatutAlerteDTO dto,
            RedirectAttributes redirectAttributes) {

        try {

            alerteService.changerStatut(id, dto);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Le statut de l'alerte a été modifié avec succès.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/alertes/" + id;
    }

    
    @PostMapping("/{id}/en-cours")
    public String marquerEnCours(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            alerteService.marquerEnCours(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Alerte marquee EN COURS.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/alertes/" + id;
    }

    
    @PostMapping("/{id}/resoudre")
    public String resoudre(
            @PathVariable Long id,
            String commentaire,
            RedirectAttributes redirectAttributes) {

        try {

            alerteService.marquerCommeResolue(id, commentaire);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Alerte resolue.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/alertes/" + id;
    }

   
    @PostMapping("/{id}/ignorer")
    public String ignorer(
            @PathVariable Long id,
            String commentaire,
            RedirectAttributes redirectAttributes) {

        try {

            alerteService.ignorerAlerte(id, commentaire);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Alerte ignorre.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/alertes/" + id;
    }

}
