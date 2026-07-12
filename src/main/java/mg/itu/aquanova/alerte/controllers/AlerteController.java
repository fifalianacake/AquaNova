package mg.itu.aquanova.alerte.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.dto.UpdateStatutAlerteDTO;
import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.alerte.services.AlerteService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;

@Controller
@RequestMapping("/alertes")
public class AlerteController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final AlerteService alerteService;
    private final LotRepository lotRepository;
    private final BassinsRepository bassinsRepository;
    private final PdfRenderService pdfRenderService;

    public AlerteController(AlerteService alerteService,
                            LotRepository lotRepository,
                            BassinsRepository bassinsRepository,
                            PdfRenderService pdfRenderService) {
        this.alerteService = alerteService;
        this.lotRepository = lotRepository;
        this.bassinsRepository = bassinsRepository;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping
    public String liste(
            @ModelAttribute("filter") AlerteFilterDTO filter,
            @PageableDefault(size = 10, sort = {"niveauCriticite", "dateCreation"},
                    direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            Model model) {

        model.addAttribute("alertes", alerteService.search(filter, pageable));
        model.addAttribute("nbCritiques", alerteService.countCritiques());
        addFilterAttributes(model);
        return "alertes/list";
    }

    @GetMapping("/historique")
    public String historique(
            @ModelAttribute("filter") AlerteFilterDTO filter,
            @PageableDefault(size = 10, sort = "dateCreation") Pageable pageable,
            Model model) {

        model.addAttribute("alertes", alerteService.searchHistorique(filter, pageable));
        addFilterAttributes(model);
        return "alertes/historique";
    }

    @GetMapping("/historique/export/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf(@ModelAttribute AlerteFilterDTO filter) {
        Map<String, String> filtres = new LinkedHashMap<>();
        if (filter != null) {
            ajouterFiltre(filtres, "Module", filter.getModuleSource());
            ajouterFiltre(filtres, "Type", filter.getTypeAlerte());
            ajouterFiltre(filtres, "Criticité", filter.getNiveauCriticite());
            ajouterFiltre(filtres, "Statut", filter.getStatut());
            ajouterFiltre(filtres, "Du", filter.getDateDebut());
            ajouterFiltre(filtres, "Au", filter.getDateFin());
        }

        Map<String, Object> modele = new HashMap<>();
        modele.put("alertes", alerteService.listerHistoriquePourExport(filter));
        modele.put("filtres", filtres);
        modele.put("sousTitre", "Alertes résolues ou ignorées");

        byte[] pdf = pdfRenderService.rendre("alertes-historique", modele);
        return PdfResponses.attachment(pdf, "historique-alertes.pdf");
    }

    private void ajouterFiltre(Map<String, String> filtres, String libelle, Object valeur) {
        if (valeur != null && !valeur.toString().isBlank()) {
            filtres.put(libelle, valeur.toString());
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("alerte", alerteService.getById(id));
        model.addAttribute("statuts", StatutAlerte.values());
        return "alertes/detail";
    }

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
