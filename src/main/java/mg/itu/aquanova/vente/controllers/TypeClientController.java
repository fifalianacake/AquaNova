package mg.itu.aquanova.vente.controllers;

import mg.itu.aquanova.vente.services.TypeClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/types-clients")
public class TypeClientController {

    private final TypeClientService typeClientService;

    public TypeClientController(TypeClientService typeClientService) {
        this.typeClientService = typeClientService;
    }

    @GetMapping
    public String listerTypes(Model model) {
        model.addAttribute("types", typeClientService.listerTout());
        return "clients/types-clients"; 
    }
}