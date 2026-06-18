package mg.itu.aquanova.referentiel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.aquanova.referentiel.modeles.EspecesModels;

public interface EspecesRepository extends JpaRepository<EspecesModels, Integer> {
}
