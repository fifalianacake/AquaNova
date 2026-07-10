package mg.itu.aquanova.production.repositories;

import java.util.List;

import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.models.StatutRecolteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoltesRepository extends JpaRepository<Recoltes, Long>, JpaSpecificationExecutor<Recoltes> {
    List<Recoltes> findByStatut(StatutRecolteEnum statut);

    List<Recoltes> findByLotId(Long lotId);
}