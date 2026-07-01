package mg.itu.aquanova.alimentation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alimentation.models.Distribution;

import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    
    List<Distribution> findByLotId(Long lotId);
}