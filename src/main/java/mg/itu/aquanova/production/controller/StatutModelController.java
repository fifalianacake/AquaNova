package mg.itu.aquanova.production.controller;

import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.production.repositories.StatutLotRepository;

@RestController
public class StatutModelController {
    private final StatutLotRepository statutLotRepository;
}
