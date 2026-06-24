package mg.itu.aquanova.production.repositories;

import mg.itu.aquanova.production.models.MouvementStock;
import mg.itu.aquanova.production.models.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    List<MouvementStock> findTop10ByAlimentIdOrderByDateMouvementDesc(Long alimentId);

    @Query("SELECT m FROM MouvementStock m WHERE " +
           "(:debut IS NULL OR m.dateMouvement >= :debut) AND " +
           "(:fin IS NULL OR m.dateMouvement <= :fin) AND " +
           "(:alimentId IS NULL OR m.aliment.id = :alimentId) AND " +
           "(:typeMvt IS NULL OR m.typeMouvement = :typeMvt)")
    List<MouvementStock> filtrerMouvements(
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin,
        @Param("alimentId") Long alimentId,
        @Param("typeMvt") TypeMouvement typeMvt
    );
}