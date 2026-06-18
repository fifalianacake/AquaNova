package mg.itu.aquanova.repository;
import mg.itu.aquanova.entity.MouvementStock;
import mg.itu.aquanova.entity.TypeMouvement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    /**
     * Stock = somme des ENTREE - somme des SORTIE/PERTE pour un alimen
     */
    @Query("""
        SELECT COALESCE(SUM(
            CASE WHEN m.typeMouvement = 'ENTREE' THEN m.quantiteKg
                 ELSE -m.quantiteKg
            END), 0)
        FROM MouvementStock m
        WHERE m.aliment.id = :alimentId
        AND m.dateMouvement <= :date
        """)
    BigDecimal calculerStock(@Param("alimentId") Long alimentId, @Param("date") LocalDate date);

    /**
     * Recherche multi-critères pour la page Liste mouvements 
     */
    @Query("""
        SELECT m FROM MouvementStock m
        WHERE (:id IS NULL OR m.id = :id)
        AND (:dateDebut IS NULL OR m.dateMouvement >= :dateDebut)
        AND (:dateFin IS NULL OR m.dateMouvement <= :dateFin)
        AND (:alimentId IS NULL OR m.aliment.id = :alimentId)
        AND (:type IS NULL OR m.typeMouvement = :type)
        ORDER BY m.dateMouvement DESC
        """)
    List<MouvementStock> search(
            @Param("id") Long id,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("alimentId") Long alimentId,
            @Param("type") TypeMouvement type
    );

    /**
     * Historique récent des mouvements d'un aliment jusqu'à une date donnée, 
     */
    @Query("""
        SELECT m FROM MouvementStock m
        WHERE m.aliment.id = :alimentId
        AND m.dateMouvement <= :date
        ORDER BY m.dateMouvement DESC
        """)
    List<MouvementStock> findRecentByAlimentAndDate(
            @Param("alimentId") Long alimentId,
            @Param("date") LocalDate date,
            Pageable pageable
    );

    /**
     * Historique récent sans filtre de date 
     */
    List<MouvementStock> findByAliment_IdOrderByDateMouvementDesc(Long alimentId, Pageable pageable);
}