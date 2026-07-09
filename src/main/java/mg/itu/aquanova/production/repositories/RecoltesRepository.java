package mg.itu.aquanova.production.repositories;

import java.util.List;

import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.models.StatutRecolteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoltesRepository extends JpaRepository<Recoltes, Long> {
    List<Recoltes> findByStatut(StatutRecolteEnum statut);
}