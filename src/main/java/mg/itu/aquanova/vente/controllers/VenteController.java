package mg.itu.aquanova.vente.controllers;

import java.util.List;

import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.TypeClientService;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.RecolteService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/ventes")
public class VenteController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final VenteService service;
    private final StatutVenteRepository statutRepository;
    private final RecolteService recolteService; // Ajouté ici
    private final LotService lotService;
    private final ClientService clientService;
    private final TypeClientService typeClientService;

    public VenteController(VenteService service, StatutVenteRepository statutRepository,
            RecolteService recolteService, LotService lotService, ClientService clientService,
            TypeClientService typeClientService) {
        this.service = service;
        this.statutRepository = statutRepository;
        this.recolteService = recolteService;
        this.lotService = lotService;
        this.clientService = clientService;
        this.typeClientService = typeClientService;
    }

    @GetMapping
    public String lister(
            @ModelAttribute("filter") TransactionFilterDTO filter,
            @PageableDefault(size = 10, sort = "dateVente") Pageable pageable,
            Model model) {

        model.addAttribute("ventes", service.lister(filter, pageable));
        model.addAttribute("statuts", statutRepository.findAll());
        model.addAttribute("recoltes", recolteService.getAllRecoltes());
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("pageSizes", PAGE_SIZES);
        return "ventes/liste";
    }

    @GetMapping("/{id}/journal")
    public String voirJournal(@PathVariable Long id, Model model) {
        model.addAttribute("vente", service.trouverParId(id));
        return "ventes/journal";
    }

    @GetMapping("/new")
    public String afficherFormulaire(Model model) {
        Vente v = new Vente();
        v.setDateVente(LocalDate.now());

        model.addAttribute("vente", v);
        model.addAttribute("recoltes", recolteService.getRecoltesDisponibles()); // Utilise le getAllRecoltes() de Tommy
        model.addAttribute("clients", clientService.listerActifsPour(null));
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
            model.addAttribute("clients", clientService.listerActifsPour(vente.getClient()));
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
        Vente vente = service.trouverParId(id);
        model.addAttribute("vente", vente);
        model.addAttribute("clients", clientService.listerActifsPour(vente.getClient()));
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
