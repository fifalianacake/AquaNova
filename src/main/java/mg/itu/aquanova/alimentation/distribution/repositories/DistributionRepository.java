package mg.itu.aquanova.alimentation.distribution.repositories;

import mg.itu.aquanova.alimentation.distribution.models.DistributionModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<DistributionModels, Long> {
    
    List<DistributionModels> findByLotId(Long lotId);
}