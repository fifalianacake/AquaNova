package mg.itu.aquanova.vente.controllers;

import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.TypeClientService;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.production.services.RecolteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/ventes")
public class VenteController {

    private final VenteService service;
    private final StatutVenteRepository statutRepository;
    private final RecolteService recolteService; // Ajouté ici
    private final ClientService clientService;
    private final TypeClientService typeClientService;

    public VenteController(VenteService service, StatutVenteRepository statutRepository,
            RecolteService recolteService, ClientService clientService, TypeClientService typeClientService) {
        this.service = service;
        this.statutRepository = statutRepository;
        this.recolteService = recolteService;
        this.clientService = clientService;
        this.typeClientService = typeClientService;
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
            @RequestParam(required = false) BigDecimal montantMin,
            @RequestParam(required = false) BigDecimal montantMax,
            Model model) {

        TransactionFilterDTO filters = new TransactionFilterDTO();
        filters.setId(id);
        filters.setClient(client);
        filters.setIdRecolte(recolteId);
        filters.setIdLot(lotId);
        filters.setDateDebut(debut);
        filters.setDateFin(fin);
        filters.setStatutId(statutId);
        filters.setMontantMin(montantMin);
        filters.setMontantMax(montantMax);

        model.addAttribute("ventes", service.search(filters));
        model.addAttribute("statuts", statutRepository.findAll());
        model.addAttribute("currentId", id);
        model.addAttribute("currentClient", client);
        model.addAttribute("currentRecolteId", recolteId);
        model.addAttribute("currentLotId", lotId);
        model.addAttribute("currentDebut", debut);
        model.addAttribute("currentFin", fin);
        model.addAttribute("currentStatutId", statutId);
        model.addAttribute("currentMontantMin", montantMin);
        model.addAttribute("currentMontantMax", montantMax);
        return "ventes/liste";
    }

    @GetMapping("/new")
    public String afficherFormulaire(Model model) {
        Vente v = new Vente();
        v.setDateVente(LocalDate.now());

        model.addAttribute("vente", v);
        model.addAttribute("recoltes", recolteService.getRecoltesDisponibles()); // Utilise le getAllRecoltes() de Tommy
        model.addAttribute("clients", clientService.rechercher(null, null, null, null, null));
        model.addAttribute("typesClient", typeClientService.listerTout());
        return "ventes/formulaire";
    }

    @PostMapping
    public String enregistrer(@ModelAttribute("vente") Vente vente, Model model) {
        try {
            if (vente.getRecolte() != null && vente.getRecolte().getId() != null) {
                vente.setRecolte(recolteService.getRecolteById(vente.getRecolte().getId()));
            }
            if (vente.getClient() != null && vente.getClient().getId() != null) {
                vente.setClient(clientService.trouverParId(vente.getClient().getId()));
            }

            if (vente.getId() == null) {
                service.create(vente);
            } else {
                service.update(vente);
            }

            return "redirect:/ventes";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("clients", clientService.rechercher(null, null, null, null, null));
        model.addAttribute("typesClient", typeClientService.listerTout());

            if (vente.getId() == null) {
                model.addAttribute("recoltes", recolteService.getRecoltesDisponibles());
                return "ventes/formulaire";
            }

            return "ventes/edit";
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
        model.addAttribute("clients", clientService.rechercher(null, null, null, null, null));
        model.addAttribute("typesClient", typeClientService.listerTout());
        return "ventes/edit";
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id, Model model) {
        try {
            service.validerVente(id);
            return "redirect:/ventes/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("vente", service.trouverParId(id));
            model.addAttribute("erreur", e.getMessage());
            return "ventes/fiche";
        }
    }

    @PostMapping("/{id}/payer")
    public String payer(@PathVariable Long id, Model model) {
        try {
            service.marquerPayee(id);
            return "redirect:/ventes/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("vente", service.trouverParId(id));
            model.addAttribute("erreur", e.getMessage());
            return "ventes/fiche";
        }
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, Model model) {
        try {
            service.annulerVente(id);
            return "redirect:/ventes/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("vente", service.trouverParId(id));
            model.addAttribute("erreur", e.getMessage());
            return "ventes/fiche";
        }
    }
}
