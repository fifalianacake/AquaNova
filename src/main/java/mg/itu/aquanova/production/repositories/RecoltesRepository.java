package mg.itu.aquanova.production.repositories;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Recoltes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoltesRepository extends JpaRepository<Recoltes, Long> {
    List<Recoltes> findByLot(LotModels lot);
}