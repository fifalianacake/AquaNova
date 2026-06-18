package mg.itu.aquanova.referentiel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.referentiel.models.EspecesModels;

public interface EspecesRepository extends JpaRepository<EspecesModels, Integer> {
}
