package mg.itu.aquanova.production.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.production.models.StatutLotModels;

@Repository
public interface StatutLotRepository extends JpaRepository<StatutLotModels, Long> {
    
}
