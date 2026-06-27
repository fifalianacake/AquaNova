package mg.itu.aquanova.alimentation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.alimentation.models.Aliment;

public interface AlimentRepository extends JpaRepository<Aliment, Integer> {

    List<Aliment> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
}
