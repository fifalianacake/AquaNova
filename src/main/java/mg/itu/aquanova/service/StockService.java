package mg.itu.aquanova.service;

import mg.itu.aquanova.entity.Aliment;
import mg.itu.aquanova.repository.AlimentRepository;
import mg.itu.aquanova.repository.MouvementStockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final AlimentRepository alimentRepository;

    public StockService(MouvementStockRepository mouvementStockRepository,
            AlimentRepository alimentRepository) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.alimentRepository = alimentRepository;
    }

    public BigDecimal getStockByAlimentAndDate(Long alimentId, LocalDate date) {
        BigDecimal stock = mouvementStockRepository.calculerStock(alimentId, date);
        return stock != null ? stock : BigDecimal.ZERO;
    }

    public BigDecimal getStockByAliment(Long alimentId) {
        return getStockByAlimentAndDate(alimentId, LocalDate.now());
    }

    public List<Object[]> getStockAtDate(LocalDate date) {
        List<Aliment> aliments = alimentRepository.findAll();
        return aliments.stream()
                .map(aliment -> new Object[] { aliment, getStockByAlimentAndDate(aliment.getId(), date) })
                .toList();
    }

    public boolean isAlerte(Aliment aliment, LocalDate date) {
        BigDecimal stock = getStockByAlimentAndDate(aliment.getId(), date);
        return stock.compareTo(aliment.getSeuilAlerteKg()) < 0;
    }

    public long countAlertes(LocalDate date) {
        return alimentRepository.findAll().stream()
                .filter(aliment -> isAlerte(aliment, date))
                .count();
    }

    public BigDecimal totalStock(LocalDate date) {
        return alimentRepository.findAll().stream()
                .map(aliment -> getStockByAlimentAndDate(aliment.getId(), date))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}