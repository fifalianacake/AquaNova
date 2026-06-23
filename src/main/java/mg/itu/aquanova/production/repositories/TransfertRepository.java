package mg.itu.aquanova.production.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.production.models.TransfertModels;

public interface TransfertRepository extends JpaRepository<TransfertModels, Long> {
    
}