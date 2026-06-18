package mg.itu.aquanova.repository;

import mg.itu.aquanova.entity.MouvementStock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    /**
     * Stock = somme des ENTREE - somme des SORTIE/PERTE pour un aliment, jusqu'à une date donnée incluse.
     * Utilisé par StockService.getStockAtDate / getStockByAlimentAndDate.
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

    // La recherche multi-critères (id, dateDebut, dateFin, alimentId, type) se fait en mémoire
    // dans MouvementService.search(), via findAll() + filtrage par streams Java.
    // Ça évite le problème Postgres "could not determine data type of parameter"
    // qu'on avait avec une requête JPQL du type (:param IS NULL OR ...).

    /**
     * Historique récent des mouvements d'un aliment jusqu'à une date donnée, le plus récent en premier.
     * Utilisé par la fiche état de stock et la fiche aliment (avec un Pageable limité, ex: PageRequest.of(0, limit)).
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
     * Historique récent sans filtre de date (utilisé par la fiche aliment / mouvementService.getRecentByAliment).
     */
    List<MouvementStock> findByAliment_IdOrderByDateMouvementDesc(Long alimentId, Pageable pageable);
}