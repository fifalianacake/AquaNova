package mg.itu.aquanova.production.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;


@Repository
public interface LotRepository extends JpaRepository<LotModels, Long>, JpaSpecificationExecutor<LotModels> {
    List<LotModels> findByBassinIdAndStatutLotLibelleNotIn(Long bassinId, Collection<StatutLotEnum> statuts);

    /** Dernier lot (code le plus élevé) dont le code commence par le préfixe donné (ex. "LOT-20260711-"). */
    Optional<LotModels> findFirstByCodeStartingWithOrderByCodeDesc(String prefixe);
}
