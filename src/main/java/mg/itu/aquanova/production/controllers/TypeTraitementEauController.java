package mg.itu.aquanova.production.controllers;

import mg.itu.aquanova.production.models.TypeTraitementEau;
import mg.itu.aquanova.production.services.TypeTraitementEauService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/production/types-traitements-eau")
public class TypeTraitementEauController {
    private final TypeTraitementEauService service;

    public TypeTraitementEauController(TypeTraitementEauService service) { this.service = service; }

    @GetMapping
    public List<TypeTraitementEau> getAll() { return service.listerTous(); }

    @GetMapping("/{id}")
    public TypeTraitementEau getById(@PathVariable Long id) { return service.trouverParId(id); }

    @PostMapping
    public TypeTraitementEau create(@RequestBody TypeTraitementEau type) { return service.create(type); }

    @PutMapping("/{id}")
    public TypeTraitementEau update(@PathVariable Long id, @RequestBody TypeTraitementEau type) { return service.update(id, type); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}