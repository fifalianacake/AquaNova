package mg.itu.aquanova.achat.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.models.TypeFournisseur;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;

@Controller
public class FournisseurController {

    private final FournisseurService service;
    private final PdfRenderService pdfRenderService;

    public FournisseurController(FournisseurService service, PdfRenderService pdfRenderService) {
        this.service = service;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping("/fournisseurs")
    public String listeFournisseurs(
        @RequestParam(value = "id", required = false) Long id,
        @RequestParam(value = "nom", required = false) String nom,
        @RequestParam(value = "typeFournisseur", required = false) String typeFournisseur,
        @RequestParam(value = "contact", required = false) String contact,
        @RequestParam(value = "actif", required = false) Boolean actif,
        Model model) {
    
    // Pour l'instant, on prépare l'appel filtré
    List<Fournisseur> fournisseurs = service.rechercherAvecFiltres(id, nom, typeFournisseur, contact, actif);
    
    model.addAttribute("fournisseurs", fournisseurs);
    return "achat_depense/fournisseur/list";
}

    
    @GetMapping("/fournisseurs/new")
    public String nouveauFournisseurForm(Model model) {
        model.addAttribute("fournisseur", new Fournisseur());
        model.addAttribute("typesFournisseur", TypeFournisseur.values()); // Pour remplir un <select> dans le formulaire
        return "achat_depense/fournisseur/formulaire";
    }

    
    @PostMapping("/fournisseurs")
    public String sauvegarderFournisseur(@ModelAttribute("fournisseur") Fournisseur fournisseur, RedirectAttributes redirectAttributes) {
        service.sauvegarder(fournisseur);
        redirectAttributes.addFlashAttribute("successMessage", "Fournisseur enregistré avec succès !");
        return "redirect:/fournisseurs";
    }

    
    @GetMapping("/fournisseurs/{id}")
    public String ficheFournisseur(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Fournisseur fournisseur = service.trouverParId(id);
            model.addAttribute("fournisseur", fournisseur);
        
            List<Achat> achats = service.listerAchatsParFournisseur(id);
            model.addAttribute("achats", achats);
        
            return "achat_depense/fournisseur/fiche";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/fournisseurs";
        }
    }

    
    @PostMapping("/fournisseurs/{id}/desactiver")
    public String desactiverFournisseur(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            service.desactiver(id);
            redirectAttributes.addFlashAttribute("successMessage", "Fournisseur désactivé.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fournisseurs";
    }

    @GetMapping("/fournisseurs/{id}/edit")
    public String modifierFournisseurForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Fournisseur fournisseur = service.trouverParId(id);
            model.addAttribute("fournisseur", fournisseur);
            model.addAttribute("typesFournisseur", TypeFournisseur.values());
            return "achat_depense/fournisseur/formulaire"; 
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/fournisseurs";
        }
    }

    
    @GetMapping("/fournisseurs/{id}/export/pdf")
    public ResponseEntity<byte[]> exporterFicheFournisseurPdf(@PathVariable("id") Long id) {
        Fournisseur fournisseur = service.trouverParId(id);
        List<Achat> achats = service.listerAchatsParFournisseur(id);

        // Seuls les achats validés constituent un engagement réel : les brouillons et les
        // achats annulés sont affichés mais exclus du total, comme dans l'historique des dépenses.
        List<Achat> valides = achats.stream()
                .filter(a -> a.getStatutAchat() == StatutAchat.VALIDE)
                .toList();
        BigDecimal totalAchats = valides.stream()
                .map(Achat::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> modele = new HashMap<>();
        modele.put("fournisseur", fournisseur);
        modele.put("achats", achats);
        modele.put("nbAchatsValides", valides.size());
        modele.put("totalAchats", totalAchats);
        modele.put("sousTitre", fournisseur.getTypeFournisseur() != null
                ? "Fournisseur " + fournisseur.getTypeFournisseur()
                : null);

        byte[] pdf = pdfRenderService.rendre("fournisseur", modele);
        return PdfResponses.attachment(pdf, "fiche-fournisseur-" + id + ".pdf");
    }
}