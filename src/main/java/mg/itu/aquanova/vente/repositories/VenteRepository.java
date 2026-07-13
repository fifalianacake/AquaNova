package mg.itu.aquanova.vente.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.vente.models.Vente;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long>, JpaSpecificationExecutor<Vente> {

    @Query("SELECT v FROM Vente v WHERE v.recolte.id = :recolteId AND v.statutVente.code <> mg.itu.aquanova.vente.models.StatutVenteEnum.ANNULEE")
    List<Vente> findActiveVentesByRecolte(@Param("recolteId") Long recolteId);

    @Query("SELECT v FROM Vente v WHERE v.client.id = :clientId ORDER BY v.dateVente DESC")
    List<Vente> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(v.poidsVendu * v.prixUnitaire), 0) FROM Vente v "
            + "WHERE v.recolte.lot.id = :lotId "
            + "AND v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE)")
    Double sumChiffreAffairesParLot(@Param("lotId") Long lotId);

    /** Poids réellement vendu d'un lot (même périmètre), pour le prix de revient au kg vendu. */
    @Query("SELECT COALESCE(SUM(v.poidsVendu), 0) FROM Vente v "
            + "WHERE v.recolte.lot.id = :lotId "
            + "AND v.statutVente.code IN (mg.itu.aquanova.vente.models.StatutVenteEnum.VALIDEE, mg.itu.aquanova.vente.models.StatutVenteEnum.PAYEE)")
    Double sumPoidsVenduParLot(@Param("lotId") Long lotId);

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
