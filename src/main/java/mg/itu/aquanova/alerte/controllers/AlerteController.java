package mg.itu.aquanova.alerte.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.alerte.dto.AlerteDTO;
import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.alerte.repositories.NiveauCriticiteRepository;
import mg.itu.aquanova.alerte.repositories.StatutAlerteRepository;
import mg.itu.aquanova.alerte.repositories.TypeAlerteRepository;
import mg.itu.aquanova.alerte.services.AlerteService;

@Controller
@RequestMapping("/alertes")
public class AlerteController {

    private final AlerteService alerteService;
    private final TypeAlerteRepository typeAlerteRepository;
    private final NiveauCriticiteRepository niveauCriticiteRepository;
    private final StatutAlerteRepository statutAlerteRepository;

    public AlerteController(AlerteService alerteService,
                            TypeAlerteRepository typeAlerteRepository,
                            NiveauCriticiteRepository niveauCriticiteRepository,
                            StatutAlerteRepository statutAlerteRepository) {
        this.alerteService = alerteService;
        this.typeAlerteRepository = typeAlerteRepository;
        this.niveauCriticiteRepository = niveauCriticiteRepository;
        this.statutAlerteRepository = statutAlerteRepository;
    }

    // Liste des alertes avec filtres
    @GetMapping
    public String listeAlertes(
            @ModelAttribute AlerteFilterDTO filter,
            Model model) {

        Page<AlerteDTO> page = this.alerteService.search(filter);
        List<AlerteDTO> critiques = this.alerteService.getAlertesCritiquesActives();
        Long nbCritiques = this.alerteService.countCritiques();

        List<TypeAlerte> typesAlertes = this.typeAlerteRepository.findAll();
        List<NiveauCriticite> niveaux = this.niveauCriticiteRepository.findAll();
        List<StatutAlerte> statuts = this.statutAlerteRepository.findAll();

        model.addAttribute("alertes", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("filter", filter);
        model.addAttribute("critiques", critiques);
        model.addAttribute("nbCritiques", nbCritiques);
        model.addAttribute("typesAlertes", typesAlertes);
        model.addAttribute("niveaux", niveaux);
        model.addAttribute("statuts", statuts);

        // Modules sources disponibles
        model.addAttribute("modules", List.of(
                "PRODUCTION", "ALIMENTATION", "SANITAIRE", "FINANCE", "VENTES"
        ));

        return "alertes/liste";
    }

    // Détail d'une alerte
    @GetMapping("/{id}")
    public String detailAlerte(@PathVariable Long id, Model model) {
        AlerteDTO alerte = this.alerteService.getById(id);
        model.addAttribute("alerte", alerte);
        return "alertes/detail";
    }
}