package mg.itu.aquanova.vente.repositories;

import mg.itu.aquanova.vente.models.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT v FROM Vente v WHERE v.recolte.id = :recolteId AND v.statutVente.code <> mg.itu.aquanova.vente.models.StatutVenteEnum.ANNULEE")
    List<Vente> findActiveVentesByRecolte(@Param("recolteId") Long recolteId);

    // Nouvelle méthode ultra-rapide pour l'historique de Sarobidy
    @Query("SELECT v FROM Vente v WHERE v.client.id = :clientId ORDER BY v.dateVente DESC")
    List<Vente> findByClientId(@Param("clientId") Long clientId);

    @Query("""
                SELECT v FROM Vente v
                JOIN v.client c
                JOIN v.recolte r
                WHERE (:id IS NULL OR v.id = :id)
                  AND (
                        :client IS NULL
                        OR LOWER(c.nom) LIKE CONCAT('%', LOWER(CAST(:client AS string)), '%')
                      )
                  AND (:recolteId IS NULL OR r.id = :recolteId)
                  AND (:lotId IS NULL OR r.lot.id = :lotId)
                  AND (:debut IS NULL OR v.dateVente >= :debut)
                  AND (:fin IS NULL OR v.dateVente <= :fin)
                  AND (:statutId IS NULL OR v.statutVente.id = :statutId)
                ORDER BY v.dateVente DESC
            """)
    List<Vente> filtrerVentes(
            @Param("id") Long id,
            @Param("client") String client,
            @Param("recolteId") Long recolteId,
            @Param("lotId") Long lotId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("statutId") Long statutId);
}