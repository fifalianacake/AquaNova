package mg.itu.aquanova.production.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
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
    public String showCreateTransfertForm(@RequestParam(value = "lotId", required = false) Long lotId, Model model) {
        TransfertModels transfert = new TransfertModels();
        if (lotId != null) {
            LotModels lotSource = lotService.trouverParId(lotId);
            transfert.setLotSource(lotSource);
            transfert.setBassinSource(lotSource.getBassin());
            transfert.setEffectif(lotSource.getEffectifActuel());
            if (lotSource.getPoidsMoyenActuel() != null) {
                transfert.setPoidsMoyen(BigDecimal.valueOf(lotSource.getPoidsMoyenActuel()));
            }
            model.addAttribute("lotPreselectionne", lotSource);
        }
        model.addAttribute("transfert", transfert);
        addFormLists(model);
        return "production/transferts/form";
    }

    @PostMapping
    public String saveTransfert(@ModelAttribute("transfert") TransfertModels transfert, Model model) {
        try {
            transfertService.saveTransfert(transfert);
            return "redirect:/transferts";
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("transfert", transfert);
            addFormLists(model);
            return "production/transferts/form";
        }
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

    private void addFormLists(Model model) {
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("bassinsLibres", bassinService.listerBassinsLibres());
        model.addAttribute("lots", lotService.listerTous());
    }
}
