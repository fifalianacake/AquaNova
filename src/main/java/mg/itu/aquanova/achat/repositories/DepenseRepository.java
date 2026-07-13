package mg.itu.aquanova.achat.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.achat.models.Depense;

public interface DepenseRepository extends JpaRepository<Depense, Long>, JpaSpecificationExecutor<Depense> {
    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Depense d "
            + "WHERE d.dateDepense BETWEEN :debut AND :fin")
    BigDecimal sumMontantEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT d.categorieDepense.libelle, COALESCE(SUM(d.montant), 0) FROM Depense d "
            + "WHERE d.dateDepense BETWEEN :debut AND :fin "
            + "GROUP BY d.categorieDepense.id, d.categorieDepense.libelle "
            + "ORDER BY SUM(d.montant) DESC")
    List<Object[]> sumMontantParCategorieEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    boolean existsByCategorieDepenseId(Long categorieId);

}
