package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alimentation.models.DistributionModels;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<DistributionModels, Long> {
    
    List<DistributionModels> findByLotId(Long lotId);

    @Query("""
            SELECT COALESCE(SUM(d.quantite), 0)
            FROM DistributionModels d
            WHERE d.aliment.id = :alimentId
              AND d.dateDistribution >= :dateDebut
              AND d.dateDistribution <= :dateFin
            """)
    Double sumQuantiteByAlimentIdAndDateBetween(
            @Param("alimentId") Long alimentId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
}
