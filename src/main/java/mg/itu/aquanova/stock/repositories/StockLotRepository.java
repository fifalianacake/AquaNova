package mg.itu.aquanova.stock.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.aquanova.stock.models.StockLot;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

    List<StockLot> findByAlimentIdAndQuantiteRestanteGreaterThanOrderByDateEntreeAsc(
        Long alimentId, Double min
    );
}