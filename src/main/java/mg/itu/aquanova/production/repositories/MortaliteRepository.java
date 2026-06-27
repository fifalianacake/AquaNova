package mg.itu.aquanova.production.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.production.models.MortaliteModels;

public interface MortaliteRepository
        extends JpaRepository<MortaliteModels, Integer> {

    List<MortaliteModels> findByLotIdOrderByDateMortaliteDesc(Long lotId);

    List<MortaliteModels> findByLotIdAndDateMortaliteBetweenOrderByDateMortaliteDesc(
            Long lotId,
            LocalDate dateDebut,
            LocalDate dateFin);

    @Query("select coalesce(sum(m.nbMorts), 0) from MortaliteModels m where m.lot.id = :lotId")
    Integer sumNbMortsByLotId(@Param("lotId") Long lotId);
}
