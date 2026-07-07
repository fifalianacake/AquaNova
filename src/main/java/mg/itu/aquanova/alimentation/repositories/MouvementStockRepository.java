package mg.itu.aquanova.alimentation.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.alimentation.models.*;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByAlimentId(Long alimentId);

    Optional<MouvementStock> findByDistributionId(Long distributionId);
}