package mg.itu.aquanova.achat.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.achat.models.LigneAchat;

public interface LigneAchatRepository extends JpaRepository<LigneAchat, Long> {
    List<LigneAchat> findByAchatId(Long achatId);
    List<LigneAchat> findByLotId(Long lotId);

    @Query("SELECT COALESCE(SUM(l.montantLigne), 0) FROM LigneAchat l WHERE l.lot.id = :lotId")
    BigDecimal sumMontantParLot(@Param("lotId") Long lotId);

    @Query("SELECT COALESCE(SUM(l.montantLigne), 0) FROM LigneAchat l "
            + "WHERE l.lot IS NOT NULL "
            + "AND l.achat.statutAchat = mg.itu.aquanova.achat.models.StatutAchat.VALIDE "
            + "AND l.achat.dateAchat BETWEEN :debut AND :fin")
    BigDecimal sumMontantAlevinsEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT l.achat.dateAchat, l.quantite, l.prixUnitaire FROM LigneAchat l "
            + "WHERE l.aliment.id = :alimentId "
            + "AND l.achat.statutAchat = mg.itu.aquanova.achat.models.StatutAchat.VALIDE "
            + "AND l.achat.dateAchat <= :date "
            + "ORDER BY l.achat.dateAchat ASC, l.id ASC")
    List<Object[]> findEntreesStockJusqua(@Param("alimentId") Long alimentId, @Param("date") LocalDate date);
}
