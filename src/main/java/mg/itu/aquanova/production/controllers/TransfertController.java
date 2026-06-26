package mg.itu.aquanova.production.controllers;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.production.models.TransfertModels;
import mg.itu.aquanova.production.services.TransfertService;
import mg.itu.aquanova.referentiel.services.BassinService; 
import mg.itu.aquanova.production.services.LotService; 

@Controller
@RequestMapping("/transferts")
public class TransfertController {

    private final TransfertService transfertService;
    private final BassinService bassinService; 
    private final LotService lotService;       

    // Injection par constructeur globale
    public TransfertController(TransfertService transfertService, BassinService bassinService, LotService lotService) {
        this.transfertService = transfertService;
        this.bassinService = bassinService;
        this.lotService = lotService;
    }

    @GetMapping
    public String getAllTransferts(Model model) {
        List<TransfertModels> transferts = transfertService.getAllTransferts();
        model.addAttribute("transferts", transferts);
        return "production/transferts/list";
    }

    @GetMapping("/new")
    public String showCreateTransfertForm(Model model) {
        model.addAttribute("transfert", new TransfertModels());
        
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("lots", lotService.listerTous());
        
        return "production/transferts/form";
    }

    @PostMapping("/save")
    public String saveTransfert(@ModelAttribute("transfert") TransfertModels transfert) {
        transfertService.saveTransfert(transfert);
        return "redirect:/transferts";
    }

    @GetMapping("/{id}")
    public String getTransfert(@PathVariable("id") Long id, Model model) {
        TransfertModels transfert = transfertService.getTransfertById(id);
        if (transfert != null) {
            model.addAttribute("transfert", transfert);
            return "production/transferts/details";
        } else {
            return "redirect:/transferts";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTransfert(@PathVariable("id") Long id) {
        transfertService.deleteTransfert(id);
        return "redirect:/transferts";
    }

    @GetMapping("/edit/{id}")
    public String showEditTransfertForm(@PathVariable("id") Long id, Model model) {
        TransfertModels transfert = transfertService.getTransfertById(id);
        if (transfert != null) {
            model.addAttribute("transfert", transfert);
            
            model.addAttribute("bassins", bassinService.getAllBassins());
            model.addAttribute("lots", lotService.listerTous());
            
            return "production/transferts/form";
        } else {
            return "redirect:/transferts";
        }
    }
}