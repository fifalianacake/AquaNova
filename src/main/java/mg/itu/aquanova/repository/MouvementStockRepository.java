package mg.itu.aquanova.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.entity.MouvementStock;

import java.time.LocalDate;
import java.util.List;

public interface MouvementStockRepository
        extends JpaRepository<MouvementStock, Long> {

    List<MouvementStock> findByAlimentIdOrderByDateMouvementDesc(Long alimentId);

    List<MouvementStock> findByAlimentIdAndDateMouvementLessThanEqualOrderByDateMouvementDesc(
            Long alimentId,
            LocalDate date);
}
