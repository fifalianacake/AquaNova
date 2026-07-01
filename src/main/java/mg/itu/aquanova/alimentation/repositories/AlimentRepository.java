package mg.itu.aquanova.alimentation.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.alimentation.models.Aliment;

public interface AlimentRepository extends JpaRepository<Aliment, Long> {
    Optional<Aliment> findByTypeAliment(TypeAlimentModels typeAliment);
}