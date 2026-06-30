package mg.itu.aquanova.alimentation.controllers;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

import mg.itu.aquanova.alimentation.dto.DistributionDTO;
import mg.itu.aquanova.alimentation.models.DistributionModels;
import mg.itu.aquanova.alimentation.services.DistributionService;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.stock.services.AlimentService;

@RequestMapping("/alimentation/distribution")
@Controller
public class DistributionController {

    private final DistributionService distributionService;
    private final LotService lotService;
    private final AlimentService alimentService;

    public DistributionController(DistributionService distributionService, 
        LotService lotService, AlimentService alimentService ) {
        this.distributionService = distributionService;
        this.lotService = lotService;
        this.alimentService = alimentService;
    }

    @GetMapping
    public String listDistributions(Model model) {
        model.addAttribute("distributions", distributionService.getAllDistributions());

        return "alimentation/distribution/list";
    }

    @GetMapping("/new")
    public String showDistributionForm(Model model) {

        model.addAttribute("distribution", new DistributionDTO());
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("aliments", alimentService.findAll());

        return "alimentation/distribution/form";
    }

    @PostMapping("/save")
    public String saveDistribution(@ModelAttribute DistributionDTO distributionDTO) {
        distributionService.saveDistribution(distributionDTO);
        return "redirect:/alimentation/distribution";
    }

    @GetMapping("/edit/{id}")
    public String editDistributionForm(@PathVariable Long id, Model model) {
        DistributionModels distribution = distributionService.getDistributionById(id);

        DistributionDTO distributionDTO = new DistributionDTO();

        distributionDTO.setId(distribution.getId());
        distributionDTO.setDateDistribution(distribution.getDateDistribution());
        distributionDTO.setIdLot(distribution.getLot() != null ? distribution.getLot().getId() : null);
        distributionDTO
                .setIdAliment(distribution.getAliment() != null ? distribution.getAliment().getId().longValue() : null);
        distributionDTO.setQuantite(distribution.getQuantite());
        distributionDTO.setRationTheorique(distribution.getRationTheorique());

        model.addAttribute("distribution", distributionDTO);
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("aliments", alimentService.findAll());

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
