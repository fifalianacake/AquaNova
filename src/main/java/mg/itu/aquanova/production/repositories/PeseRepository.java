package mg.itu.aquanova.production.repositories;


import mg.itu.aquanova.production.models.Pese;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeseRepository extends JpaRepository<Pese, Long>, JpaSpecificationExecutor<Pese> {
    
    // Récupère les pesées d'un lot triées de la plus récente à la plus ancienne
    List<Pese> findByLotIdOrderByDatePeseeDesc(Long idLot);

    // Récupère les pesées d'un lot triées chronologiquement
    List<Pese> findByLotIdOrderByDatePeseeAsc(Long idLot);
}
