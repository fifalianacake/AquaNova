package mg.itu.aquanova.alimentation.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mg.itu.aquanova.alimentation.services.StockService;

@Controller
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService service;

    @GetMapping
    public String list(String nom, String date, Integer page, Integer size, Model model) {

        LocalDate d = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        model.addAttribute("stocks",
                service.getAllStocks(d, nom, page != null ? page : 0, size != null ? size : 10));

        model.addAttribute("total", service.totalStock(d));
        model.addAttribute("date", d);

        return "alimentation/stocks/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
            @RequestParam(required = false) String date,
            Model model) {

        LocalDate d = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        model.addAttribute("stock", service.getStock(id, d));
        model.addAttribute("mouvements", service.getHistorique(id));
        model.addAttribute("mouvements", service.getHistorique(id));

        return "alimentation/stocks/detail";
    }
}