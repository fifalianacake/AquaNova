package mg.itu.aquanova.sanitaire.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import mg.itu.aquanova.referentiel.services.BassinService;
import mg.itu.aquanova.sanitaire.models.ReleveEau;
import mg.itu.aquanova.sanitaire.services.ReleveEauService;
import mg.itu.aquanova.security.models.UserModels;

@Controller
@RequestMapping("/releves-eau")
public class ReleveEauController {

    @Autowired
    private ReleveEauService service;

    @Autowired
    private BassinService bassinService;

    /*
     * LIST
     */

    @GetMapping
    public String list(Long id,
            String bassin,
            String start,
            String end,
            Double minTemp,
            Double maxTemp,
            Double minPh,
            Double maxPh,
            Double minOxy,
            Double maxOxy,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("releves",
                service.search(id, bassin, start, end,
                        minTemp, maxTemp,
                        minPh, maxPh,
                        minOxy, maxOxy));

        return "sanitaire/releves/list";
    }

    /*
     * NEW FORM
     */

    @GetMapping("/new")
    public String createForm(Model model,
            HttpSession session) {

        UserModels user = (UserModels) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");

        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        ReleveEau releve = new ReleveEau();
        releve.setDateReleve(LocalDate.now());

        model.addAttribute("releve", releve);
        model.addAttribute("bassins", bassinService.getAllBassins());

        return "sanitaire/releves/new";
    }

    /*
     * SAVE
     */

    @PostMapping
    public String save(@ModelAttribute ReleveEau releve,
            HttpSession session) {

        UserModels user = (UserModels) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        String role = (String) session.getAttribute("role");

        if (!"ADMIN".equals(role))
            return "redirect:/releves-eau";

        releve.setUser(user);

        service.create(releve);

        return "redirect:/releves-eau";
    }

    /*
     * DETAIL
     */

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("releve",
                service.getById(id));

        return "sanitaire/releves/detail";
    }

}