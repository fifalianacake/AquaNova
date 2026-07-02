package mg.itu.aquanova.sanitaire_equipement.controllers;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
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
import mg.itu.aquanova.sanitaire_equipement.services.MaintenanceService;
import mg.itu.aquanova.sanitaire_equipement.services.CategorieMaintenanceService;
import mg.itu.aquanova.sanitaire_equipement.services.MaintenanceFilter;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

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
            @PageableDefault(size = 10) Pageable pageable, 
            Model model) {
        
        Page<Maintenance> pageMaintenances = maintenanceService.lister(filter, pageable);
        
        model.addAttribute("maintenances", pageMaintenances.getContent());
        model.addAttribute("page", pageMaintenances);
        
        model.addAttribute("filter", filter); 
        return "sanitaire/maintenance/list"; 
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("maintenance", new Maintenance());
        model.addAttribute("equipements", equipementService.findAll());
        model.addAttribute("categories", categorieMaintenanceService.getAll());
        return "maintenance/form";
    }

    @PostMapping("/save")
    public String saveMaintenance(@ModelAttribute("maintenance") Maintenance maintenance, Model model) {
        try {
            maintenanceService.create(maintenance);
            return "redirect:/maintenances";
        } catch (IllegalArgumentException e) {
            model.addAttribute("maintenance", maintenance);
            model.addAttribute("errorMessage", e.getMessage());
            return "maintenance/form"; 
        }
    }

    @GetMapping("/{id}")
    public String getMaintenanceDetails(@PathVariable("id") Long id, Model model) {
        Maintenance maintenance = maintenanceService.findById(id);
        
        if (maintenance == null) {
            throw new RuntimeException("Maintenance introuvable avec l'id : " + id);
        }
        
        model.addAttribute("maintenance", maintenance);
        return "sanitaire/maintenance/detail";
    }

    @PostMapping("/update/{id}")
    public String updateMaintenance(@PathVariable("id") Long id, @ModelAttribute("maintenance") Maintenance maintenance, Model model) {
        try {
            maintenanceService.update(id, maintenance);
            return "redirect:/maintenances/" + id;
        } catch (Exception e) {
            model.addAttribute("maintenance", maintenance);
            model.addAttribute("errorMessage", e.getMessage());
            return "sanitaire/maintenance/form";
        }
    }

    @PostMapping("/{id}/cloturer")
    public String cloturerIntervention(
            @PathVariable("id") Long id,
            @RequestParam(value = "observation", required = false) String observation,
            @RequestParam(value = "coutFinal", required = false) BigDecimal coutFinal) {
        
        maintenanceService.cloturerIntervention(id, observation, coutFinal);
        return "redirect:/maintenances/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteMaintenance(@PathVariable("id") Long id) {
        maintenanceService.delete(id);
        return "redirect:/maintenances";
    }

    @GetMapping("/equipement/{idEquipement}")
    public String getHistoriqueEquipement(@PathVariable("idEquipement") Long idEquipement, Model model) {
        model.addAttribute("maintenances", maintenanceService.getByEquipement(idEquipement));
        return "maintenance/historique_equipement";
    }
}