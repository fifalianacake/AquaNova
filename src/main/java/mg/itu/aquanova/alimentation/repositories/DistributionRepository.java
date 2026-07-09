package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alimentation.models.Distribution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    
    @Query("""
            SELECT SUM(d.quantite)
            FROM Distribution d
            WHERE d.aliment.id = :alimentId
              AND d.dateDistribution >= :dateDebut
              AND d.dateDistribution <= :dateFin
            """)
    BigDecimal sumQuantiteByAlimentIdAndDateBetween(
            @Param("alimentId") Long alimentId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    List<Distribution> findByLotId(Long lotId);

    @Query("SELECT COALESCE(SUM(d.quantite * d.aliment.prixUnitaire), 0) " +
           "FROM Distribution d WHERE d.lot.id = :lotId")
    Double findTotalCoutAlimentByLotId(@Param("lotId") Long lotId);

    // Prix moyen pondéré (Ar/kg) de l'aliment consommé par un lot, sur tout son historique de distribution.
    @Query("SELECT CASE WHEN COALESCE(SUM(d.quantite), 0) = 0 THEN 0.0 " +
           "ELSE SUM(d.quantite * d.aliment.prixUnitaire) / SUM(d.quantite) END " +
           "FROM Distribution d WHERE d.lot.id = :lotId")
    Double findPrixMoyenAlimentByLotId(@Param("lotId") Long lotId);
}
