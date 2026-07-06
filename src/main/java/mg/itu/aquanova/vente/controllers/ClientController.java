package mg.itu.aquanova.vente.controllers;

import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.TypeClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final TypeClientService typeClientService;

    public ClientController(ClientService clientService, TypeClientService typeClientService) {
        this.clientService = clientService;
        this.typeClientService = typeClientService;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) Boolean actif,
            Model model) {

        model.addAttribute("clients", clientService.rechercher(id, nom, typeId, contact, actif));
        model.addAttribute("types", typeClientService.listerTout());
        return "clients/liste";
    }

    @GetMapping("/new")
    public String afficherFormulaireCreation(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("types", typeClientService.listerTout());
        return "clients/formulaire";
    }

    @PostMapping
    public String enregistrer(@ModelAttribute("client") Client client, Model model) {
        try {
            clientService.enregistrer(client);
            return "redirect:/clients";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("types", typeClientService.listerTout());
            return "clients/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String voirFiche(@PathVariable Long id, Model model) {
        Client c = clientService.trouverParId(id);
        model.addAttribute("client", c);
        model.addAttribute("ventes", clientService.obtenirHistoriqueVentes(id));
        model.addAttribute("caTotal", clientService.calculerChiffreAffaires(id));
        return "clients/fiche";
    }
    @PostMapping("/{id}/edit")
    public String enregistrerModification(@PathVariable Long id, @ModelAttribute("client") Client client, Model model) {
        try {
            client.setId(id); 
            clientService.enregistrer(client);
            return "redirect:/clients/" + id; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("types", typeClientService.listerTout());
            return "clients/edit";
        }
    }

    @GetMapping("/{id}/edit")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.trouverParId(id));
        model.addAttribute("types", typeClientService.listerTout());
        return "clients/edit";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerOuDesactiver(@PathVariable Long id) {
        clientService.supprimerOuDesactiver(id);
        return "redirect:/clients";
    }
}