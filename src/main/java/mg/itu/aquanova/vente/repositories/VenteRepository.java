package mg.itu.aquanova.vente.repositories;

import mg.itu.aquanova.vente.models.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT v FROM Vente v WHERE v.recolte.id = :recolteId AND v.statutVente.code <> 'ANNULEE'")
    List<Vente> findActiveVentesByRecolte(@Param("recolteId") Long recolteId);

    @Query("SELECT v FROM Vente v WHERE " +
           "(:id IS NULL OR v.id = :id) AND " +
           "(:client IS NULL OR LOWER(v.client) LIKE LOWER(CONCAT('%', :client, '%'))) AND " +
           "(:recolteId IS NULL OR v.recolte.id = :recolteId) AND " +
           "(:lotId IS NULL OR v.recolte.lot.id = :lotId) AND " +
           "(:debut IS NULL OR v.dateVente >= :debut) AND " +
           "(:fin IS NULL OR v.dateVente <= :fin) AND " +
           "(:statutId IS NULL OR v.statutVente.id = :statutId) " +
           "ORDER BY v.dateVente DESC")
    List<Vente> filtrerVentes(
        @Param("id") Long id,
        @Param("client") String client,
        @Param("recolteId") Long recolteId,
        @Param("lotId") Long lotId,
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin,
        @Param("statutId") Long statutId
    );
}