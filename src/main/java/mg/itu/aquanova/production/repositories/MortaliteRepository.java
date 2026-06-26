package mg.itu.aquanova.production.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.production.models.MortaliteModels;

public interface MortaliteRepository
        extends JpaRepository<MortaliteModels, Integer> {

    List<MortaliteModels> findByIdLotOrderByDateMortaliteDesc(Integer idLot);

    List<MortaliteModels> findByIdLotAndDateMortaliteBetweenOrderByDateMortaliteDesc(
            Integer idLot,
            LocalDate dateDebut,
            LocalDate dateFin);

    @Query("select coalesce(sum(m.nbMorts), 0) from MortaliteModels m where m.idLot = :idLot")
    Integer sumNbMortsByIdLot(@Param("idLot") Integer idLot);
}
