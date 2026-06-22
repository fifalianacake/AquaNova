package mg.itu.aquanova.referentiel.repositories;

import mg.itu.aquanova.referentiel.models.TypeBassin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeBassinRepository extends JpaRepository<TypeBassin, Long> {
}
