package mg.itu.aquanova.production.repositories;
import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.models.TypeEvenementLot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JournalLotRepository extends JpaRepository<JournalLot, Long> {
    List<JournalLot> findByLotIdOrderByDateEvenementAsc(Long lotId);

    List<JournalLot> findAllByOrderByDateEvenementAsc();

    @Query("""
            select j from JournalLot j
            where (:lotId is null or j.lot.id = :lotId)
              and (:typeEvenement is null or j.typeEvenement.libelle = :typeEvenement)
              and (:dateDebut is null or j.dateEvenement >= :dateDebut)
              and (:dateFin is null or j.dateEvenement <= :dateFin)
            order by j.dateEvenement asc
            """)
    List<JournalLot> rechercher(
            @Param("lotId") Long lotId,
            @Param("typeEvenement") TypeEvenementLot.LibelleEvenement typeEvenement,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);
}
