package mg.itu.aquanova.referentiel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.referentiel.models.TypeAlimentModels;

public interface TypeAlimentRepository
        extends JpaRepository<TypeAlimentModels, Integer> {
}