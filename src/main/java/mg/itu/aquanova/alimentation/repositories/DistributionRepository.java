package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alimentation.models.DistributionModels;

import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<DistributionModels, Long> {
    
    List<DistributionModels> findByLotId(Long lotId);
}