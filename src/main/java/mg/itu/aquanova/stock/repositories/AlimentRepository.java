package mg.itu.aquanova.stock.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.stock.models.Aliment;

public interface AlimentRepository extends JpaRepository<Aliment, Long> {
    Optional<Aliment> findByTypeAliment(TypeAlimentModels typeAliment);
}