package mg.itu.aquanova.alerte.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.alerte.models.Alerte;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    // Alertes actives (ni RESOLUE ni IGNOREE), triées criticité DESC
    @Query("SELECT a FROM Alerte a " +
           "WHERE a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE') " +
           "ORDER BY a.niveauCriticite.ordre DESC, a.dateCreation DESC")
    List<Alerte> findAlertesActives();



    // Alertes critiques uniquement
    @Query("SELECT a FROM Alerte a " +
           "WHERE a.niveauCriticite.code = 'CRITIQUE' " +
           "AND a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE') " +
           "ORDER BY a.dateCreation DESC")
    List<Alerte> findAlertesCritiquesActives();



    // Compte alertes critiques actives (pour le badge dashboard)
    @Query("SELECT COUNT(a) FROM Alerte a " +
           "WHERE a.niveauCriticite.code = 'CRITIQUE' " +
           "AND a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE')")
    Long countAlertesCritiquesActives();

    

    // Recherche avec filtres multi-critères + pagination
    @Query("SELECT a FROM Alerte a " +
           "WHERE (:moduleSource IS NULL OR a.moduleSource = :moduleSource) " +
           "AND (:typeAlerte IS NULL OR a.typeAlerte.code = :typeAlerte) " +
           "AND (:niveauCriticite IS NULL OR a.niveauCriticite.code = :niveauCriticite) " +
           "AND (:statutAlerte IS NULL OR a.statutAlerte.code = :statutAlerte) " +
           "AND (:dateDebut IS NULL OR a.dateCreation >= :dateDebut) " +
           "AND (:dateFin IS NULL OR a.dateCreation <= :dateFin) " +
           "AND (:motCle IS NULL OR LOWER(a.message) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
           "ORDER BY a.niveauCriticite.ordre DESC, a.dateCreation DESC")
    Page<Alerte> searchAlertes(
            @Param("moduleSource") String moduleSource,
            @Param("typeAlerte") String typeAlerte,
            @Param("niveauCriticite") String niveauCriticite,
            @Param("statutAlerte") String statutAlerte,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("motCle") String motCle,
            Pageable pageable);

    // Vérifier si une alerte active existe déjà pour la même entité + type
    @Query("SELECT COUNT(a) > 0 FROM Alerte a " +
           "WHERE a.typeAlerte.code = :typeCode " +
           "AND a.entiteType = :entiteType " +
           "AND a.entiteId = :entiteId " +
           "AND a.statutAlerte.code NOT IN ('RESOLUE', 'IGNOREE')")
    boolean existsAlerteActiveForEntite(
            @Param("typeCode") String typeCode,
            @Param("entiteType") String entiteType,
            @Param("entiteId") Long entiteId);
}