package mg.itu.aquanova.alimentation.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.alimentation.dto.StockDTO;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.alimentation.repositories.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository repo;

    @Autowired
    private AlimentRepository alimentRepository;

    public Page<StockDTO> getAllStocks(LocalDate date, String nom, int page, int size) {

        List<Aliment> aliments = alimentRepository.findAll();

        if (nom != null && !nom.isBlank()) {
            aliments = aliments.stream()
                    .filter(a -> a.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<StockDTO> stocks = aliments.stream()
                .map(a -> getStock(a.getId(), date))
                .collect(Collectors.toList());

        int start = Math.min(page * size, stocks.size());
        int end = Math.min(start + size, stocks.size());

        return new PageImpl<>(
                stocks.subList(start, end),
                PageRequest.of(page, size),
                stocks.size());
    }

    public StockDTO getStock(Long id, LocalDate date) {

        Aliment aliment = alimentRepository.findById(id)
                .orElseThrow();

        double stock = 0;

        for (MouvementStock m : repo.findByAlimentId(id)) {

            if (m.getDateMouvement().isAfter(date))
                continue;

            if (m.getTypeMouvement() == TypeMouvement.ENTREE) {
                stock += m.getQuantite();
            } else {
                stock -= m.getQuantite();
            }
        }

        return new StockDTO(
                aliment.getId(),
                aliment.getNom(),
                stock,
                100.0);
    }

    public double getStockAtDate(Long id, LocalDate date) {
        return getStock(id, date).getStock();
    }

    public double totalStock(LocalDate date) {

        double total = 0;

        for (Aliment a : alimentRepository.findAll()) {
            total += getStockAtDate(a.getId(), date);
        }

        return total;
    }

    public List<MouvementStock> getHistorique(Long id) {
        return repo.findByAlimentId(id);
    }
}