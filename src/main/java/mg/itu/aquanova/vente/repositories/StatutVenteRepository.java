package mg.itu.aquanova.vente.repositories;

import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatutVenteRepository extends JpaRepository<StatutVente, Long> {

    StatutVente findByCode(StatutVenteEnum code);
    
}