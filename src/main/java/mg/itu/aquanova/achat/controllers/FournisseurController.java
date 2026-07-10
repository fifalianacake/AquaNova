package mg.itu.aquanova.achat.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import mg.itu.aquanova.achat.models.TypeFournisseur;
import mg.itu.aquanova.achat.services.FournisseurService;
import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfExportService;

@Controller
public class FournisseurController {

    private final FournisseurService service;
    private final PdfExportService pdfExportService;

    public FournisseurController(FournisseurService service, PdfExportService pdfExportService) {
        this.service = service;
        this.pdfExportService = pdfExportService;
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
    // 1. Récupération des données métiers depuis ton service habituel
    Fournisseur fournisseur = service.trouverParId(id);
    
    // Imaginons que tu récupères la liste d'achats liée via ton service
    List<Achat> achats = service.listerAchatsParFournisseur(id);

    // 2. Préparation des champs de la section principale (Informations Générales)
    Map<String, String> infosGenerales = new LinkedHashMap<>();
    infosGenerales.put("ID du fournisseur", fournisseur.getId().toString());
    infosGenerales.put("Type", fournisseur.getTypeFournisseur() != null ? fournisseur.getTypeFournisseur().name() : "-");
    infosGenerales.put("Contact référent", fournisseur.getContact());
    infosGenerales.put("Adresse Email", fournisseur.getEmail());
    infosGenerales.put("Adresse physique", fournisseur.getAdresse());
    infosGenerales.put("NIF / STAT", fournisseur.getNifStat());
    infosGenerales.put("Statut de l'activité", fournisseur.getActif() ? "Actif (Ouvert)" : "Inactif (Désactivé)");
    infosGenerales.put("Observation", fournisseur.getObservation());

    // 3. Préparation des lignes du tableau de l'historique d'achats
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    List<List<String>> lignesAchats = achats.stream().map(achat -> {
    // On prépare chaque variable sous forme de String pure
    String ref = (achat.getReferenceFacture() != null) ? achat.getReferenceFacture() : "-";
    
    String date = "-";
    if (achat.getDateAchat() != null) {
        // Si c'est un LocalDateTime ou LocalDate
        date = achat.getDateAchat().format(formatter); 
        // Si c'est une java.util.Date classique, utilise plutôt :
        // date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(achat.getDateAchat());
    }
    
    String montant = (achat.getMontantTotal() != null) ? achat.getMontantTotal().toString() + " Ar" : "0 Ar";
    String statut = (achat.getStatutAchat() != null) ? achat.getStatutAchat().name() : "-";

    // On retourne explicitement une liste de String
    return List.of(ref, date, montant, statut);
    }).collect(Collectors.toList());

    // 4. Construction de l'objet de données fluide (le DTO)
    FichePdfData pdfData = FichePdfData.of("Fiche Fournisseur : " + fournisseur.getNom())
        .sousTitre("Code interne AquaNova : #FOURN-" + fournisseur.getId())
        .section("Informations Générales", infosGenerales)
        .table("Historique des achats effectués", 
               List.of("Référence Achat", "Date", "Montant Total", "Statut"), 
               lignesAchats);

    // 5. Génération du tableau d'octets (byte[]) par ton service partagé
    byte[] pdfBytes = pdfExportService.genererFiche(pdfData);

    // 6. Envoi de la réponse avec les bons en-têtes grâce à ta classe utilitaire
    String nomFichier = "fiche_fournisseur_" + id + ".pdf";
    return PdfResponses.attachment(pdfBytes, nomFichier);
}
}