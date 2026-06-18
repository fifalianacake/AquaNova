package mg.itu.aquanova.referentiel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;

public interface StadeCroissanceRepository
        extends JpaRepository<StadeCroissanceModels, Integer> {
}