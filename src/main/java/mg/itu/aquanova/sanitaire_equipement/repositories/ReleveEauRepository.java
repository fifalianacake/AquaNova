package mg.itu.aquanova.sanitaire_equipement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.sanitaire_equipement.models.ReleveEau;

public interface ReleveEauRepository extends JpaRepository<ReleveEau, Long>, JpaSpecificationExecutor<ReleveEau> {

    List<ReleveEau> findByBassinId(Long bassinId);

}