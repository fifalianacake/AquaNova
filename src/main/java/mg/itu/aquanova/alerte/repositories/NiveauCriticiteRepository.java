package mg.itu.aquanova.alerte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alerte.models.NiveauCriticite;

@Repository
public interface NiveauCriticiteRepository extends JpaRepository<NiveauCriticite, Long> {
    Optional<NiveauCriticite> findByCode(String code);
}