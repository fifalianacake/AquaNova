package mg.itu.aquanova.alerte.repositories;

import mg.itu.aquanova.alerte.models.SeuilAlerte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeuilAlerteRepository extends JpaRepository<SeuilAlerte, Long> {

    // Utilisé pour getByCode() et getSeuilActif()
    Optional<SeuilAlerte> findByCode(String code);

    // Gère la fonction de filtrage complète (search)
    @Query("SELECT s FROM SeuilAlerte s WHERE " +
           "(:moduleSource IS NULL OR LOWER(s.moduleSource) = LOWER(:moduleSource)) AND " +
           "(:code IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:actif IS NULL OR s.actif = :actif)")
    Page<SeuilAlerte> filtrerSeuils(
            @Param("moduleSource") String moduleSource,
            @Param("code") String code,
            @Param("actif") Boolean actif,
            Pageable pageable
    );
}