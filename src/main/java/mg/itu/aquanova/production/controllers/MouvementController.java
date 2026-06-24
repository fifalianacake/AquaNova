package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.MouvementStock;
import mg.itu.aquanova.production.models.TypeMouvement;
import mg.itu.aquanova.production.services.MouvementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/production/stocks/mouvements")
public class MouvementController {
    private final MouvementService service;

    public MouvementController(MouvementService service) {
        this.service = service;
    }

    @GetMapping
    public List<MouvementStock> getMouvements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) Long alimentId,
            @RequestParam(required = false) TypeMouvement typeMvt) {
        return service.search(debut, fin, alimentId, typeMvt);
    }

    @GetMapping("/{id}")
    public MouvementStock getById(@PathVariable Long id) {
        return service.trouverParId(id);
    }

    @GetMapping("/recent/aliment/{alimentId}")
    public List<MouvementStock> getRecent(@PathVariable Long alimentId) {
        return service.getRecentByAliment(alimentId);
    }

    @PostMapping
    public MouvementStock save(@RequestBody MouvementStock mvt) {
        return service.create(mvt);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PutMapping("/{id}")
    public MouvementStock update(@PathVariable Long id, @RequestBody MouvementStock mvtDetails) {
        // Il faudra juste s'assurer que la méthode existe ou soit ajoutée dans votre MouvementService
        return service.update(id, mvtDetails); 
    }
}