package mg.itu.aquanova.alerte.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alerte.models.Alerte;

/**
 * Repository pour l'entité Alerte.
 * Étend JpaSpecificationExecutor pour permettre les requêtes dynamiques
 * via Specification (filtres combinables de l'historique).
 */
@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long>, JpaSpecificationExecutor<Alerte> {
}
