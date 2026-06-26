package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.services.TypeEvenementLotService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/production/types-evenements")
public class TypeEvenementLotController {
    private final TypeEvenementLotService service;

    public TypeEvenementLotController(TypeEvenementLotService service) { this.service = service; }

    @GetMapping
    public List<TypeEvenementLot> getAll() { return service.listerTous(); }

    @GetMapping("/{id}")
    public TypeEvenementLot getById(@PathVariable Long id) { return service.trouverParId(id); }

    @PostMapping
    public TypeEvenementLot create(@RequestBody TypeEvenementLot type) { return service.creer(type); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.supprimer(id); }
}