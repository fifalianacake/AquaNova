package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.aquanova.alimentation.models.StadeCroissanceModels;

public interface StadeCroissanceRepository
        extends JpaRepository<StadeCroissanceModels, Integer> {
}