package mg.itu.aquanova.referentiel.repositories;

import mg.itu.aquanova.referentiel.models.Bassin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BassinsRepository extends JpaRepository<Bassin, Long> {
    // Permet de vérifier facilement si une référence existe déjà
    Optional<Bassin> findByReference(String reference);
}
