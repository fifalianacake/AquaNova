package mg.itu.aquanova.referentiel.repository;

import com.aquanova.model.TypeBassin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeBassinRepository extends JpaRepository<TypeBassin, Long> {
}
