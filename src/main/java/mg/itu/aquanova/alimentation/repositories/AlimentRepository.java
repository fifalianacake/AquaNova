package mg.itu.aquanova.alimentation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.alimentation.models.AlimentModel;

public interface AlimentRepository extends JpaRepository<AlimentModel, Integer> {

    List<AlimentModel> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
}
