package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.TraitementEau;
import mg.itu.aquanova.production.services.TraitementEauService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/production/traitements-eau")
public class TraitementEauController {
    private final TraitementEauService service;

    public TraitementEauController(TraitementEauService service) {
        this.service = service;
    }

    @GetMapping
    public List<TraitementEau> getTraitements(
            @RequestParam(required = false) Long id, // Ajouté ici
            @RequestParam(required = false) Long bassinId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return service.search(id, bassinId, typeId, debut, fin);
    }

    @GetMapping("/{id}")
    public TraitementEau getById(@PathVariable Long id) {
        return service.trouverParId(id);
    }

    @GetMapping("/bassin/{bassinId}")
    public List<TraitementEau> getByBassin(@PathVariable Long bassinId) {
        return service.getByBassin(bassinId);
    }

    @PostMapping
    public TraitementEau save(@RequestBody TraitementEau traitement) {
        return service.create(traitement);
    }

    @PutMapping("/{id}")
    public TraitementEau update(@PathVariable Long id, @RequestBody TraitementEau traitement) {
        return service.update(id, traitement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}