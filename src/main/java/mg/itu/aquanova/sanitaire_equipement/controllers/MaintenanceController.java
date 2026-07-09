package mg.itu.aquanova.sanitaire_equipement.controllers;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.sanitaire_equipement.models.Maintenance;
import mg.itu.aquanova.sanitaire_equipement.models.StatutInterventionEnum;
import mg.itu.aquanova.sanitaire_equipement.services.MaintenanceService;
import mg.itu.aquanova.sanitaire_equipement.services.CategorieMaintenanceService;
import mg.itu.aquanova.sanitaire_equipement.services.EquipementService;
import mg.itu.aquanova.sanitaire_equipement.services.MaintenanceFilter;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final MaintenanceService maintenanceService;
    private final EquipementService equipementService;
    private final CategorieMaintenanceService categorieMaintenanceService;

    public MaintenanceController(
        MaintenanceService maintenanceService,
        EquipementService equipementService,
        CategorieMaintenanceService categorieMaintenanceService
    ) {
        this.maintenanceService = maintenanceService;
        this.equipementService = equipementService;
        this.categorieMaintenanceService = categorieMaintenanceService;
    }

    @GetMapping
    public String listMaintenances(
            @ModelAttribute("filter") MaintenanceFilter filter,
            @PageableDefault(size = 10, sort = "dateMaintenance") Pageable pageable,
            Model model) {

        model.addAttribute("maintenances", maintenanceService.lister(filter, pageable));
        addListAttributes(model);
        return "sanitaire_equipement/maintenance/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("maintenance", new Maintenance());
        addFormAttributes(model);
        return "sanitaire_equipement/maintenance/form";
    }

    @PostMapping
    public String saveMaintenance(@ModelAttribute("maintenance") Maintenance maintenance, Model model) {
        try {
            maintenanceService.create(maintenance);
            return "redirect:/maintenances";
        } catch (IllegalArgumentException e) {
            model.addAttribute("maintenance", maintenance);
            model.addAttribute("errorMessage", e.getMessage());
            addFormAttributes(model);
            return "sanitaire_equipement/maintenance/form"; 
        }
    }

    @GetMapping("/{id}")
    public String getMaintenanceDetails(@PathVariable("id") Long id, Model model) {
        Maintenance maintenance = maintenanceService.findById(id);
        
        if (maintenance == null) {
            throw new RuntimeException("Maintenance introuvable avec l'id : " + id);
        }
        
        model.addAttribute("maintenance", maintenance);
        return "sanitaire_equipement/maintenance/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Maintenance maintenance = maintenanceService.findById(id);

        // Une intervention clôturée est une entrée d'historique figée : rien à modifier.
        if (maintenance.getStatutIntervention() == StatutInterventionEnum.TERMINEE) {
            return "redirect:/maintenances/" + id;
        }

        model.addAttribute("maintenance", maintenance);
        addFormAttributes(model);
        return "sanitaire_equipement/maintenance/form";
    }

    @PostMapping("/{id}")
    public String updateMaintenance(@PathVariable("id") Long id, @ModelAttribute("maintenance") Maintenance maintenance, Model model) {
        try {
            maintenanceService.update(id, maintenance);
            return "redirect:/maintenances/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("maintenance", maintenance);
            model.addAttribute("errorMessage", e.getMessage());
            addFormAttributes(model);
            return "sanitaire_equipement/maintenance/form";
        }
    }

    @PostMapping("/{id}/cloturer")
    public String cloturerIntervention(
            @PathVariable("id") Long id,
            @RequestParam(value = "observation", required = false) String observation,
            @RequestParam(value = "coutFinal", required = false) BigDecimal coutFinal,
            Model model) {

        try {
            maintenanceService.cloturerIntervention(id, observation, coutFinal);
            return "redirect:/maintenances/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("maintenance", maintenanceService.findById(id));
            model.addAttribute("errorMessage", e.getMessage());
            return "sanitaire_equipement/maintenance/detail";
        }
    }

    // @GetMapping("/equipement/{idEquipement}")
    // public String getHistoriqueEquipement(@PathVariable("idEquipement") Long idEquipement, Model model) {
    //     model.addAttribute("maintenances", maintenanceService.getByEquipement(idEquipement));
    //     return "maintenance/historique_equipement";
    // }

    private void addFormAttributes(Model model) {
        model.addAttribute("equipements", equipementService.listerTout());
        model.addAttribute("categories", categorieMaintenanceService.getAll());
    }

    private void addListAttributes(Model model) {
        model.addAttribute("pageSizes", PAGE_SIZES);
    }
}
