package mg.itu.aquanova.finance.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.itu.aquanova.finance.dto.CoutRevientLotDTO;
import mg.itu.aquanova.finance.services.CoutRevientService;
import mg.itu.aquanova.production.services.LotFilter;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final CoutRevientService coutRevientService;

    public FinanceController(CoutRevientService coutRevientService) {
        this.coutRevientService = coutRevientService;
    }

    @GetMapping("/lots")
    public String listerLotsAvecCout(
            @ModelAttribute("lotFilter") LotFilter lotFilter,
            Pageable pageable,
            Model model) {
        Page<CoutRevientLotDTO> page = coutRevientService.listerLotsAvecCout(lotFilter, pageable);
        model.addAttribute("lots", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("hasPrevious", page.hasPrevious());
        model.addAttribute("hasNext", page.hasNext());
        model.addAttribute("lotFilter", lotFilter);
        return "finance/lots/list";
    }
}
