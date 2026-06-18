package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.alimentation.models.TypeAlimentModels;

public interface TypeAlimentRepository
        extends JpaRepository<TypeAlimentModels, Integer> {
}