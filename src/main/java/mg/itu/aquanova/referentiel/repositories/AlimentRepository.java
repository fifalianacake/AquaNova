package mg.itu.aquanova.referentiel.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.referentiel.models.Aliment;

public interface AlimentRepository extends JpaRepository<Aliment, Long> {
    Optional<Aliment> findByTypeAliment(TypeAlimentModels typeAliment);
    List<Aliment> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
}