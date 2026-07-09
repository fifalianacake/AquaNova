package mg.itu.aquanova.alerte.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alerte.models.HistoriqueAlerte;

@Repository
public interface HistoriqueAlerteRepository
        extends JpaRepository<HistoriqueAlerte, Long> {

    List<HistoriqueAlerte> findByAlerteIdOrderByDateChangementDesc(Long idAlerte);

}