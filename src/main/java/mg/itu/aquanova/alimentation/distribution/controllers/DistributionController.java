package mg.itu.aquanova.alimentation.distribution.controllers;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

import mg.itu.aquanova.alimentation.distribution.models.DistributionModels;
import mg.itu.aquanova.alimentation.distribution.services.DistributionService;

@RequestMapping("/alimentation/distribution")
@Controller
public class DistributionController {

    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @GetMapping
    public String listDistributions(Model model) {
        model.addAttribute("distributions", distributionService.getAllDistributions());

        return "alimentation/distribution/list";
    }

    @GetMapping("/new")
    public String showDistributionForm(Model model) {

        model.addAttribute("distribution", new DistributionModels());
        model.addAttribute("lots", distributionService.getAllDistributions()); // Assuming you have a method to get all lots
        model.addAttribute("aliments", distributionService.getAllDistributions()); // Assuming you have

        return "alimentation/distribution/form";
    }

    @PostMapping("/save")
    public String saveDistribution(DistributionModels distribution) {
        distributionService.saveDistribution(distribution);
        return "redirect:/alimentation/distribution";
    }

    @GetMapping("/edit/{id}")
    public String editDistributionForm(@PathVariable Long id, Model model) {
        DistributionModels distribution = distributionService.getDistributionById(id);

        model.addAttribute("distribution", distribution);
        model.addAttribute("lots", distributionService.getAllDistributions()); // Assuming you have a method to get all lots
        model.addAttribute("aliments", distributionService.getAllDistributions()); // Assuming you have
        
        return "alimentation/distribution/form";
    }

    @GetMapping("/{id}")
    public String viewDistribution(@PathVariable Long id, Model model) {
        DistributionModels distribution = distributionService.getDistributionById(id);
        model.addAttribute("distribution", distribution);
        return "alimentation/distribution/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteDistribution(@PathVariable Long id) {
        distributionService.deleteDistribution(id);
        return "redirect:/alimentation/distribution";
    }

}
