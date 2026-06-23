package mg.itu.aquanova.production.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.production.models.LotModels;


@Repository
public interface LotRepository extends JpaRepository<LotModels, Long>, JpaSpecificationExecutor<LotModels> {
    
}
