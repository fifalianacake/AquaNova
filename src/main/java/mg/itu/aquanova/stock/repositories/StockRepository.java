package mg.itu.aquanova.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.stock.models.MouvementStock;

public interface StockRepository extends JpaRepository<MouvementStock, Long> {

    List<MouvementStock> findByAlimentId(Long id);

}