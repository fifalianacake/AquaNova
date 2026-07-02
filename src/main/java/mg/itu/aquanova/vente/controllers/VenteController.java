package mg.itu.aquanova.vente.controllers;

import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.production.services.RecolteService; 
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/ventes")
public class VenteController {

    private final VenteService service;
    private final StatutVenteRepository statutRepository;
    private final RecolteService recolteService; // Ajouté ici

    public VenteController(VenteService service, StatutVenteRepository statutRepository, RecolteService recolteService) {
        this.service = service;
        this.statutRepository = statutRepository;
        this.recolteService = recolteService;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String client,
            @RequestParam(required = false) Long recolteId,
            @RequestParam(required = false) Long lotId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) Long statutId,
            Model model) {

        model.addAttribute("ventes", service.search(id, client, recolteId, lotId, debut, fin, statutId));
        model.addAttribute("statuts", statutRepository.findAll());
        model.addAttribute("currentId", id);
        model.addAttribute("currentClient", client);
        model.addAttribute("currentRecolteId", recolteId);
        model.addAttribute("currentLotId", lotId);
        model.addAttribute("currentDebut", debut);
        model.addAttribute("currentFin", fin);
        model.addAttribute("currentStatutId", statutId);
        return "ventes/liste";

    @GetMapping("/new")
    public String afficherFormulaire(Model model) {
        Vente v = new Vente();
        v.setDateVente(LocalDate.now());
        
        model.addAttribute("vente", v);
        model.addAttribute("recoltes", recolteService.getAllRecoltes()); // Utilise le getAllRecoltes() de Tommy
        return "ventes/formulaire";
    }

    @PostMapping
    public String enregistrer(@ModelAttribute("vente") Vente vente, Model model) {
        try {
            if (vente.getId() == null) service.create(vente);
            else service.update(vente);
            return "redirect:/ventes";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("recoltes", recolteService.getAllRecoltes());
            return "ventes/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String voirFiche(@PathVariable Long id, Model model) {
        model.addAttribute("vente", service.trouverParId(id));
        return "ventes/fiche";
    }

    @GetMapping("/{id}/edit")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("vente", service.trouverParId(id));
        return "ventes/edit";
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id) {
        service.validerVente(id);
        return "redirect:/ventes/" + id;
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id) {
        service.annulerVente(id);
        return "redirect:/ventes/" + id;
    }
}