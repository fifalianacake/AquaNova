package mg.itu.aquanova.achat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.achat.models.Intrant;

public interface IntrantRepository extends JpaRepository<Intrant, Long>, JpaSpecificationExecutor<Intrant> {
    List<Intrant> findByActifTrueOrderByNomAsc();
    List<Intrant> findAllByOrderByNomAsc();
    boolean existsByNomIgnoreCase(String nom);
}
