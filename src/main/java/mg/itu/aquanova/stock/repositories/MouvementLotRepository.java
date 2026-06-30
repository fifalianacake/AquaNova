package mg.itu.aquanova.stock.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.aquanova.stock.models.MouvementLot;

public interface MouvementLotRepository extends JpaRepository<MouvementLot, Long> {

    List<MouvementLot> findByMouvementId(Long mouvementId);
}