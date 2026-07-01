package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.alimentation.repositories.MouvementStockRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class StockService {

    private final MouvementStockRepository mouvementStockRepository;

    public StockService(MouvementStockRepository mouvementStockRepository) {
        this.mouvementStockRepository = mouvementStockRepository;
    }

    public BigDecimal getStockAtDate(Long alimentId, LocalDate date) {
        if (alimentId == null || date == null) {
            throw new IllegalArgumentException("alimentId et date sont obligatoires");
        }

        BigDecimal entrees = safe(this.mouvementStockRepository
                .sumByAlimentAndTypeAndDate(alimentId, TypeMouvement.ENTREE, date));
        BigDecimal sorties = safe(this.mouvementStockRepository
                .sumByAlimentAndTypeAndDate(alimentId, TypeMouvement.SORTIE, date));
        BigDecimal pertes = safe(this.mouvementStockRepository
                .sumByAlimentAndTypeAndDate(alimentId, TypeMouvement.PERTE, date));

        BigDecimal stock = entrees.subtract(sorties).subtract(pertes);

        return stock.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : stock;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}