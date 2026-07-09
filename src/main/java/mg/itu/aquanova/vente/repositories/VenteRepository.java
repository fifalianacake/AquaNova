package mg.itu.aquanova.vente.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.vente.models.Vente;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT v FROM Vente v WHERE v.recolte.id = :recolteId AND v.statutVente.code <> mg.itu.aquanova.vente.models.StatutVenteEnum.ANNULEE")
    List<Vente> findActiveVentesByRecolte(@Param("recolteId") Long recolteId);

    @Query("SELECT v FROM Vente v WHERE " +
           "(:id IS NULL OR v.id = :id) AND " +
           "(:clientPattern IS NULL OR LOWER(v.client.nom) LIKE :clientPattern) AND " +
           "(:recolteId IS NULL OR v.recolte.id = :recolteId) AND " +
           "(:lotId IS NULL OR v.recolte.lot.id = :lotId) AND " +
           "(:debut IS NULL OR v.dateVente >= :debut) AND " +
           "(:fin IS NULL OR v.dateVente <= :fin) AND " +
           "(:statutId IS NULL OR v.statutVente.id = :statutId) AND " +
           "(:montantMin IS NULL OR (v.poidsVendu * v.prixUnitaire) >= :montantMin) AND " +
           "(:montantMax IS NULL OR (v.poidsVendu * v.prixUnitaire) <= :montantMax) " +
           "ORDER BY v.dateVente DESC")
    List<Vente> searchTransactions(
        @Param("id") Long id,
        @Param("clientPattern") String clientPattern,
        @Param("recolteId") Long recolteId,
        @Param("lotId") Long lotId,
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin,
        @Param("statutId") Long statutId,
        @Param("montantMin") Double montantMin,
        @Param("montantMax") Double montantMax
    );

    @Query("SELECT v FROM Vente v WHERE v.client.id = :clientId ORDER BY v.dateVente DESC")
    List<Vente> findByClientId(@Param("clientId") Long clientId);
     // CA total (montantTotal = poidsVendu * prixUnitaire)
    @Query("SELECT COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin")
    Double sumChiffreAffaires(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    // Volume écoulé en kg, restreint aux ventes confirmées (VALIDEE/PAYEE) — même périmètre que sumChiffreAffaires,
    // pour que le prix moyen (CA / volume) reste cohérent.
    @Query("SELECT COALESCE(SUM(v.poidsVendu), 0) FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin")
    Double sumVolumeEcoule(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    @Query("SELECT CASE WHEN COALESCE(SUM(v.poidsVendu), 0) = 0 THEN 0.0 " +
           "ELSE SUM(v.poidsVendu * v.prixUnitaire) / SUM(v.poidsVendu) END " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE)")
    Double estimerPrixMoyenVenteKg();

    // Même calcul, restreint aux ventes des lots de l'espèce donnée (via vente -> recolte -> lot -> espece).
    @Query("SELECT CASE WHEN COALESCE(SUM(v.poidsVendu), 0) = 0 THEN 0.0 " +
           "ELSE SUM(v.poidsVendu * v.prixUnitaire) / SUM(v.poidsVendu) END " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.recolte.lot.espece.id = :especeId")
    Double estimerPrixMoyenVenteKgParEspece(@Param("especeId") Integer especeId);

    // Nombre de ventes
    @Query("SELECT COUNT(v) FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin")
    Long countVentes(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    // CA par jour → courbe
    @Query("SELECT CAST(v.dateVente AS string), " +
           "COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin " +
           "GROUP BY v.dateVente ORDER BY v.dateVente ASC")
    List<Object[]> findCaParJour(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    // Volume par lot → barres (ventes confirmées uniquement, même périmètre que les autres agrégats du dashboard)
    @Query("SELECT v.recolte.lot.code, " +
           "COALESCE(SUM(v.poidsVendu), 0) " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin " +
           "GROUP BY v.recolte.lot.code " +
           "ORDER BY SUM(v.poidsVendu) DESC")
    List<Object[]> findVolumeParLot(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
            

    // Répartition CA par client (v.client est une entité Client : on sélectionne
    @Query("SELECT v.client.nom, " +
           "COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin " +
           "GROUP BY v.client.id, v.client.nom " +
           "ORDER BY SUM(v.poidsVendu * v.prixUnitaire) DESC")
    List<Object[]> findCaParClient(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);


    // Top 5 clients par CA
    @Query("SELECT v.client.nom, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.poidsVendu), 0), " +
           "COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin " +
           "GROUP BY v.client.id, v.client.nom " +
           "ORDER BY SUM(v.poidsVendu * v.prixUnitaire) DESC " +
           "LIMIT 5")
    List<Object[]> findTop5ClientsParCa(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);


    // Volume par récolte (ventes confirmées uniquement)
    @Query("SELECT v.recolte.lot.code, v.recolte.id, " +
           "COALESCE(SUM(v.poidsVendu), 0), " +
           "COALESCE(SUM(v.effectifVendu), 0), " +
           "COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) " +
           "FROM Vente v " +
           "WHERE v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE) " +
           "AND v.dateVente BETWEEN :debut AND :fin " +
           "GROUP BY v.recolte.lot.code, v.recolte.id")
    List<Object[]> findVolumeParLotEtRecolte(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}
