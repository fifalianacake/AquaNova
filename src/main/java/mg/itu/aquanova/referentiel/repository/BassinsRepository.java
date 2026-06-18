package mg.itu.aquanova.referentiel.repository;

import com.aquanova.model.Bassins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BassinsRepository extends JpaRepository<Bassins, Long> {
    // Permet de vérifier facilement si une référence existe déjà
    Optional<Bassins> findByReference(String reference);
}
