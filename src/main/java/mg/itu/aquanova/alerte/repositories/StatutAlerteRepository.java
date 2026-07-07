package mg.itu.aquanova.alerte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alerte.models.StatutAlerte;

@Repository
public interface StatutAlerteRepository extends JpaRepository<StatutAlerte, Long> {
    Optional<StatutAlerte> findByCode(String code);
}