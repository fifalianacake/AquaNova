package mg.itu.aquanova.production.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;


@Repository
public interface LotRepository extends JpaRepository<LotModels, Long>, JpaSpecificationExecutor<LotModels> {
    List<LotModels> findByBassinIdAndStatutLotLibelleNot(Long bassinId, StatutLotEnum statut);
}
