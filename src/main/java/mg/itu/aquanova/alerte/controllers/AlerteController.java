package mg.itu.aquanova.alerte.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.itu.aquanova.alerte.dto.UpdateStatutAlerteDTO;
import mg.itu.aquanova.alerte.services.AlerteService;

@Controller
@RequestMapping("/alertes")
public class AlerteController {

    private final AlerteService alerteService;

    public AlerteController(AlerteService alerteService) {
        this.alerteService = alerteService;
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