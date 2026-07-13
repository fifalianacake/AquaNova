package mg.itu.aquanova.sanitaire_equipement.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.sanitaire_equipement.dto.ReleveEauFilter;
import mg.itu.aquanova.sanitaire_equipement.models.ReleveEau;
import mg.itu.aquanova.sanitaire_equipement.services.ReleveEauService;
import mg.itu.aquanova.security.models.User;

@Controller
@RequestMapping("/releves-eau")
public class ReleveEauController {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50, 100);

    @Autowired
    private ReleveEauService service;

    @Autowired
    private BassinService bassinService;

    @GetMapping
    public String list(
            @ModelAttribute("filter") ReleveEauFilter filter,
            @PageableDefault(size = 10, sort = "dateReleve") Pageable pageable,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("releves", service.lister(filter, pageable));
        addListAttributes(model);

        return "sanitaire_equipement/releves/list";
    }

    private void addListAttributes(Model model) {
        model.addAttribute("bassins", bassinService.getAllBassins());
        model.addAttribute("pageSizes", PAGE_SIZES);
    }

    @GetMapping("/new")
    public String createForm(Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");

        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        ReleveEau releve = new ReleveEau();
        releve.setDateReleve(LocalDate.now());

        model.addAttribute("releve", releve);
        model.addAttribute("bassins", bassinService.getAllBassins());

        return "sanitaire_equipement/releves/form";
    }

    @PostMapping
    public String save(@ModelAttribute ReleveEau releve,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");

        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        releve.setUser(user);

        service.create(releve);

        return "redirect:/releves-eau";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("releve",
                service.getById(id));

        return "sanitaire_equipement/releves/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        model.addAttribute("releve", service.getById(id));
        model.addAttribute("bassins", bassinService.getAllBassins());

        return "sanitaire_equipement/releves/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute ReleveEau releve,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        User user = (User) session.getAttribute("user");

        releve.setId(id);
        releve.setUser(user);

        service.update(releve);

        return "redirect:/releves-eau";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";
        if (!"ADMIN".equals(session.getAttribute("role")))
            return "redirect:/releves-eau";

        service.delete(id);
        return "redirect:/releves-eau";
    }

}