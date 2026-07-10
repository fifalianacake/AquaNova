package mg.itu.aquanova.finance.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @PageableDefault(size = 10, page = 0, sort = "id") Pageable pageable,
            @ModelAttribute("lotFilter") LotFilter lotFilter,
            Model model) {
        Page<CoutRevientLotDTO> page = coutRevientService.listerLotsAvecCout(lotFilter, pageable);
        model.addAttribute("page", page);
        model.addAttribute("lots", page.getContent());
        model.addAttribute("lotFilter", lotFilter);
        return "finance/lots/list";
    }
}
