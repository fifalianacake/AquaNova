package mg.itu.aquanova.production.controllers;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.dto.RecolteFilter;
import mg.itu.aquanova.production.services.RecolteService;
import mg.itu.aquanova.production.services.TypeRecolteService;
import mg.itu.aquanova.production.dto.RecolteForm;
import mg.itu.aquanova.production.services.LotService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/recoltes")
public class RecolteController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    private final RecolteService recolteService;
    private final LotService lotService;
    private final TypeRecolteService typeRecolteService;

    public RecolteController(
            RecolteService recolteService,
            LotService lotService,
            TypeRecolteService typeRecolteService) {
        this.recolteService = recolteService;
        this.lotService = lotService;
        this.typeRecolteService = typeRecolteService;
    }

    @GetMapping
    public String list(
            @ModelAttribute("filter") RecolteFilter filter,
            @PageableDefault(size = 10, sort = "dateRecolte") Pageable pageable,
            Model model) {

        model.addAttribute("recoltes", recolteService.lister(filter, pageable));
        addListAttributes(model);
        return "production/recoltes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("recolteForm", new RecolteForm());
        addFormLists(model);
        return "production/recoltes/form";
    }

    @PostMapping
    public String create(@ModelAttribute("recolteForm") RecolteForm form, Model model) {
        try {
            recolteService.creerRecolte(
                    form.getLotId(),
                    form.getTypeRecolteId(),
                    form.getDateRecolte(),
                    form.getEffectifRecolte());
            return "redirect:/recoltes";
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            addFormLists(model);
            return "production/recoltes/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("recolte", recolteService.getRecolteById(id));
        return "production/recoltes/details";
    }

    private void addFormLists(Model model) {
        model.addAttribute("lots", lotService.listerTous());
        model.addAttribute("typesRecolte", typeRecolteService.getAllTypeRecoltes());
    }

    private void addListAttributes(Model model) {
        addFormLists(model);
        model.addAttribute("pageSizes", PAGE_SIZES);
    }
}
