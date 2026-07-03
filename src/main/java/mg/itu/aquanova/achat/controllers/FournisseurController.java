package mg.itu.aquanova.achat.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.TypeFournisseur;
import mg.itu.aquanova.achat.services.FournisseurService;

@Controller
public class FournisseurController {

    private final FournisseurService service;

    public FournisseurController(FournisseurService service) {
        this.service = service;
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
}