package mg.itu.aquanova.alerte.controllers;

import mg.itu.aquanova.alerte.dto.SeuilAlerteDTO;
import mg.itu.aquanova.alerte.models.SeuilAlerte;
import mg.itu.aquanova.alerte.services.SeuilAlerteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alertes/configuration")
public class SeuilAlerteController {

    private final SeuilAlerteService seuilAlerteService;

    public SeuilAlerteController(SeuilAlerteService seuilAlerteService) {
        this.seuilAlerteService = seuilAlerteService;
    }

    @GetMapping
    public String listSeuils(
            @RequestParam(required = false) String moduleSource,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean actif,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        String filterModule = (moduleSource != null && !moduleSource.trim().isEmpty()) ? moduleSource.trim() : null;
        String filterCode = (code != null && !code.trim().isEmpty()) ? code.trim() : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<SeuilAlerte> seuilsPage = seuilAlerteService.search(filterModule, filterCode, actif, pageable);

        model.addAttribute("seuilsPage", seuilsPage);
        model.addAttribute("moduleSource", moduleSource);
        model.addAttribute("code", code);
        model.addAttribute("actif", actif);
        model.addAttribute("size", size);

        return "alertes/configuration/liste";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("seuilDTO")) {
            model.addAttribute("seuilDTO", new SeuilAlerteDTO());
        }
        return "alertes/configuration/formulaire";
    }

    @PostMapping("/new")
    public String createSeuil(@ModelAttribute("seuilDTO") SeuilAlerteDTO dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        // Validation manuelle en code pur des champs obligatoires
        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            result.rejectValue("code", "error.code", "Le code est obligatoire.");
        }
        if (dto.getLibelle() == null || dto.getLibelle().trim().isEmpty()) {
            result.rejectValue("libelle", "error.libelle", "Le libellé est obligatoire.");
        }
        if (dto.getModuleSource() == null || dto.getModuleSource().trim().isEmpty()) {
            result.rejectValue("moduleSource", "error.moduleSource", "Le module source est obligatoire.");
        }
        if (dto.getValeur() == null) {
            result.rejectValue("valeur", "error.valeur", "La valeur est obligatoire.");
        }

        // Si on a des erreurs de validation, on recharge le formulaire
        if (result.hasErrors()) {
            return "alertes/configuration/formulaire";
        }

        try {
            SeuilAlerte seuil = new SeuilAlerte();
            seuil.setCode(dto.getCode().toUpperCase().trim());
            seuil.setLibelle(dto.getLibelle().trim());
            seuil.setModuleSource(dto.getModuleSource().trim());
            seuil.setValeur(dto.getValeur());
            seuil.setUnite(dto.getUnite());
            seuil.setDescription(dto.getDescription());
            seuil.setActif(dto.getActif() != null ? dto.getActif() : true);

            seuilAlerteService.create(seuil);
            redirectAttributes.addFlashAttribute("successMessage", "Le seuil a été créé avec succès.");
            return "redirect:/alertes/configuration";
        } catch (IllegalArgumentException e) {
            result.rejectValue("code", "error.code", e.getMessage());
            return "alertes/configuration/formulaire";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        SeuilAlerte seuil = seuilAlerteService.search(null, null, null, Pageable.unpaged())
                .stream().filter(s -> s.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RuntimeException("Seuil introuvable"));

        SeuilAlerteDTO dto = new SeuilAlerteDTO(
                seuil.getId(), seuil.getCode(), seuil.getLibelle(),
                seuil.getModuleSource(), seuil.getValeur(), seuil.getUnite(),
                seuil.getDescription(), seuil.getActif()
        );
        model.addAttribute("seuilDTO", dto);
        model.addAttribute("isEdit", true);
        return "alertes/configuration/formulaire";
    }

    @PostMapping("/{id}/edit")
    public String updateSeuil(@PathVariable Long id,
                              @ModelAttribute("seuilDTO") SeuilAlerteDTO dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        // Validation manuelle pour la modification
        if (dto.getLibelle() == null || dto.getLibelle().trim().isEmpty()) {
            result.rejectValue("libelle", "error.libelle", "Le libellé est obligatoire.");
        }
        if (dto.getModuleSource() == null || dto.getModuleSource().trim().isEmpty()) {
            result.rejectValue("moduleSource", "error.moduleSource", "Le module source est obligatoire.");
        }
        if (dto.getValeur() == null) {
            result.rejectValue("valeur", "error.valeur", "La valeur est obligatoire.");
        }

        if (result.hasErrors()) {
            return "alertes/configuration/formulaire";
        }

        SeuilAlerte seuil = new SeuilAlerte();
        seuil.setLibelle(dto.getLibelle().trim());
        seuil.setModuleSource(dto.getModuleSource().trim());
        seuil.setValeur(dto.getValeur());
        seuil.setUnite(dto.getUnite());
        seuil.setDescription(dto.getDescription());
        seuil.setActif(dto.getActif() != null ? dto.getActif() : false);

        seuilAlerteService.update(id, seuil);
        redirectAttributes.addFlashAttribute("successMessage", "Le seuil a été mis à jour.");
        return "redirect:/alertes/configuration";
    }

    @PostMapping("/{id}/disable")
    public String disableSeuil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        seuilAlerteService.deleteOrDisable(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le seuil a été désactivé.");
        return "redirect:/alertes/configuration";
    }
}