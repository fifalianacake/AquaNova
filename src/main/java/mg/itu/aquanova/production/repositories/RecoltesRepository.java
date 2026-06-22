package mg.itu.aquanova.production.repositories;

import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecoltesRepository extends JpaRepository<Recoltes, Long> {
}