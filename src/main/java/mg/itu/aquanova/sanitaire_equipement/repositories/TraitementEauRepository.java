package mg.itu.aquanova.sanitaire_equipement.repositories;

import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TraitementEauRepository extends JpaRepository<TraitementEau, Long> {

    List<TraitementEau> findByBassinIdOrderByDateTraitementDesc(Long bassinId);
    
    List<TraitementEau> findByTypeTraitementEauId(Long typeId);

    @Query("SELECT t FROM TraitementEau t WHERE " +
           "(:id IS NULL OR t.id = :id) AND " +
           "(:bassinId IS NULL OR t.bassin.id = :bassinId) AND " +
           "(:typeId IS NULL OR t.typeTraitementEau.id = :typeId) AND " +
           "(:debut IS NULL OR t.dateTraitement >= :debut) AND " +
           "(:fin IS NULL OR t.dateTraitement <= :fin) " +
           "ORDER BY t.dateTraitement DESC")
    List<TraitementEau> filtrerTraitements(
        @Param("id") Long id,
        @Param("bassinId") Long bassinId,
        @Param("typeId") Long typeId,
        @Param("debut") LocalDate debut,
        @Param;("fin") LocalDate fin
    );
}