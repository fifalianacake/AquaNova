package mg.itu.aquanova.alerte.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.alerte.models.Alerte;

public interface AlerteRepository extends JpaRepository<Alerte,Long>, JpaSpecificationExecutor<Alerte> {

    List<Alerte> findByModuleSource(String moduleSource);

    List<Alerte> findByStatutAlerteCode(String code);

    List<Alerte> findByTypeAlerteCode(String code);

    List<Alerte> findByNiveauCriticiteCode(String code);

    List<Alerte> findByEntiteTypeAndEntiteId(
            String entiteType,
            Long entiteId);

    @Query("""
    SELECT a FROM Alerte a
    WHERE a.statutAlerte.code NOT IN ('RESOLUE','IGNOREE')
    ORDER BY a.niveauCriticite.ordre DESC,
             a.dateCreation DESC
    """)
    List<Alerte> findAlertesActives();

    @Query("""
    SELECT a FROM Alerte a
    WHERE a.niveauCriticite.code='CRITIQUE'
    AND a.statutAlerte.code NOT IN ('RESOLUE','IGNOREE')
    ORDER BY a.dateCreation DESC
    """)
    List<Alerte> findAlertesCritiquesActives();

    @Query("""
    SELECT COUNT(a)
    FROM Alerte a
    WHERE a.niveauCriticite.code='CRITIQUE'
    AND a.statutAlerte.code NOT IN ('RESOLUE','IGNOREE')
    """)
    Long countAlertesCritiquesActives();

    @Query("""
    SELECT a
    FROM Alerte a
    WHERE
    (:moduleSource IS NULL OR a.moduleSource=:moduleSource)
    AND (:typeAlerte IS NULL OR a.typeAlerte.code=:typeAlerte)
    AND (:niveau IS NULL OR a.niveauCriticite.code=:niveau)
    AND (:statut IS NULL OR a.statutAlerte.code=:statut)
    AND (:dateDebut IS NULL OR a.dateCreation>=:dateDebut)
    AND (:dateFin IS NULL OR a.dateCreation<=:dateFin)
    AND (:motCle IS NULL
        OR LOWER(a.message)
        LIKE LOWER(CONCAT('%',:motCle,'%')))
    ORDER BY
    a.niveauCriticite.ordre DESC,
    a.dateCreation DESC
    """)
    Page<Alerte> searchAlertes(
            @Param("moduleSource") String moduleSource,
            @Param("typeAlerte") String typeAlerte,
            @Param("niveau") String niveau,
            @Param("statut") String statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("motCle") String motCle,
            Pageable pageable);

    @Query("""
    SELECT COUNT(a)>0
    FROM Alerte a
    WHERE
    a.moduleSource=:module
    AND a.typeAlerte.code=:type
    AND a.entiteType=:entiteType
    AND a.entiteId=:entiteId
    AND a.statutAlerte.code NOT IN ('RESOLUE','IGNOREE')
    """)
    boolean existsAlerteActive(
            @Param("module") String module,
            @Param("type") String type,
            @Param("entiteType") String entiteType,
            @Param("entiteId") Long entiteId);

   
    @Query("""
    SELECT a FROM Alerte a
    LEFT JOIN FETCH a.typeAlerte
    LEFT JOIN FETCH a.niveauCriticite
    LEFT JOIN FETCH a.statutAlerte
    WHERE a.id = :id
    """)
    Alerte findByIdWithRelations(@Param("id") Long id);

    
    @Query("""
    SELECT a FROM Alerte a
    WHERE a.moduleSource = :moduleSource
    AND a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE')
    ORDER BY a.dateCreation DESC
    """)
    List<Alerte> findAlertesActivesByModuleSource(@Param("moduleSource") String moduleSource);

    @Query("""
    SELECT a FROM Alerte a
    WHERE a.typeAlerte.code = :typeCode
    AND a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE')
    ORDER BY a.dateCreation DESC
    """)
    List<Alerte> findAlertesActivesByType(@Param("typeCode") String typeCode);

    
    @Query("""
    SELECT a FROM Alerte a
    WHERE a.statutAlerte.code = :statutCode
    ORDER BY a.dateCreation DESC
    """)
    List<Alerte> findAlertesByStatut(@Param("statutCode") String statutCode);

    // * Récupère les alertes actives n
    @Query("""
    SELECT a FROM Alerte a
    WHERE a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE')
    AND a.dateCreation <= :dateLimite
    ORDER BY a.dateCreation ASC
    """)
    List<Alerte> findAlertesNonResoluesDepuis(@Param("dateLimite") LocalDateTime dateLimite);

    ///reherhe
    @Query("""
    SELECT a FROM Alerte a
    WHERE a.statutAlerte.code IN ('RESOLUE', 'IGNOREE')
    AND (:moduleSource IS NULL OR a.moduleSource = :moduleSource)
    AND (:typeAlerte IS NULL OR a.typeAlerte.code = :typeAlerte)
    AND (:niveau IS NULL OR a.niveauCriticite.code = :niveau)
    AND (:statut IS NULL OR a.statutAlerte.code = :statut)
    AND (:dateDebut IS NULL OR a.dateCreation >= :dateDebut)
    AND (:dateFin IS NULL OR a.dateCreation <= :dateFin)
    AND (:entiteType IS NULL OR a.entiteType = :entiteType)
    AND (:entiteId IS NULL OR a.entiteId = :entiteId)
    ORDER BY a.dateCreation DESC
    """)
    Page<Alerte> searchHistoriqueAlertes(
            @Param("moduleSource") String moduleSource,
            @Param("typeAlerte") String typeAlerte,
            @Param("niveau") String niveau,
            @Param("statut") String statut,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("entiteType") String entiteType,
            @Param("entiteId") Long entiteId,
            Pageable pageable);

   //  * Statistiques : Nombre d'alertes par statut
    @Query("""
    SELECT a.statutAlerte.code, COUNT(a)
    FROM Alerte a
    GROUP BY a.statutAlerte.code
    """)
    List<Object[]> countAlertesByStatut();

    
     //Statistiques : Nombre d'alertes par niveau
    @Query("""
    SELECT a.niveauCriticite.code, COUNT(a)
    FROM Alerte a
    GROUP BY a.niveauCriticite.code
    """)
    List<Object[]> countAlertesByNiveauCriticite();


     //Statistiques : Nombre d'alertes par module source
         @Query("""
    SELECT a.moduleSource, COUNT(a)
    FROM Alerte a
    GROUP BY a.moduleSource
    """)
    List<Object[]> countAlertesByModuleSource();

    //  Statistiques : Nombre d'alertes par type
     
    @Query("""
    SELECT a.typeAlerte.code, a.typeAlerte.libelle, COUNT(a)
    FROM Alerte a
    GROUP BY a.typeAlerte.code, a.typeAlerte.libelle
    """)
    List<Object[]> countAlertesByType();
}
