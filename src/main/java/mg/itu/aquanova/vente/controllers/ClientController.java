package mg.itu.aquanova.vente.controllers;

import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.TypeClient;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.TypeClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

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

    // Création rapide depuis la modale du formulaire de vente (appelée en AJAX,
    // ne doit jamais rediriger : renvoie du JSON dans tous les cas).
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> creerRapide(
            @RequestParam String nom,
            @RequestParam Long typeClientId,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String adresse) {

        try {
            Client client = new Client();
            client.setNom(nom);
            TypeClient type = new TypeClient();
            type.setId(typeClientId);
            client.setTypeClient(type);
            client.setContact(contact);
            client.setEmail(email);
            client.setAdresse(adresse);

            Client sauvegarde = clientService.enregistrer(client);

            Map<String, Object> corps = new LinkedHashMap<>();
            corps.put("id", sauvegarde.getId());
            corps.put("nom", sauvegarde.getNom());
            return ResponseEntity.ok(corps);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erreur = new LinkedHashMap<>();
            erreur.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
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