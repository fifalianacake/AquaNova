package mg.itu.aquanova.production.controller;

import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.production.repositories.LotRepository;

@RestController
public class LotController {
    private final LotRepository lotRepository;
    
}
