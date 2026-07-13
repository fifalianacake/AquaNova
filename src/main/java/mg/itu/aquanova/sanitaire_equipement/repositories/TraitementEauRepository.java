package mg.itu.aquanova.sanitaire_equipement.repositories;

import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface TraitementEauRepository
        extends JpaRepository<TraitementEau, Long>, JpaSpecificationExecutor<TraitementEau> {

    List<TraitementEau> findByBassinIdOrderByDateTraitementDesc(Long bassinId);

    List<TraitementEau> findByTypeTraitementEauId(Long typeId);
}
