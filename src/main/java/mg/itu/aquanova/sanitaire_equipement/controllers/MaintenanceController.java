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
import mg.itu.aquanova.sanitaire_equipement.services.MaintenanceFilter;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public String listMaintenances(
            @ModelAttribute("filter") MaintenanceFilter filter, 
            @PageableDefault(size = 10) Pageable pageable, 
            Model model) {
        
        Page<Maintenance> pageMaintenances = maintenanceService.lister(filter, pageable);
        
        model.addAttribute("maintenances", pageMaintenances.getContent());
        model.addAttribute("page", pageMaintenances);
        model.addAttribute("pannesOuvertes", maintenanceService.getMaintenancesOuvertes()); 
        
        return "maintenance/list"; 
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("maintenance", new Maintenance());
        return "maintenance/form";
    }

    @PostMapping("/save")
    public String saveMaintenance(@ModelAttribute("maintenance") Maintenance maintenance) {
        try {
            maintenanceService.create(maintenance);
            return "redirect:/maintenances";
        } catch (IllegalArgumentException e) {
            return "maintenance/form"; 
        }
    }

    @GetMapping("/{id}")
    public String getMaintenanceDetails(@PathVariable("id") Long id, Model model) {
        // Idéalement, ajoutez un findById dans votre service pour alimenter la fiche
        // Ici, on récupère via la logique attendue
        // model.addAttribute("maintenance", maintenanceService.findById(id));
        return "maintenance/detail";
    }

    @PostMapping("/update/{id}")
    public String updateMaintenance(@PathVariable("id") Long id, @ModelAttribute("maintenance") Maintenance maintenance) {
        maintenanceService.update(id, maintenance);
        return "redirect:/maintenances/" + id;
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