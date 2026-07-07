package mg.itu.aquanova.vente.repositories;

import mg.itu.aquanova.vente.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE " +
           "(:id IS NULL OR c.id = :id) AND " +
           "(:nomPattern IS NULL OR LOWER(c.nom) LIKE :nomPattern) AND " +
           "(:typeId IS NULL OR c.typeClient.id = :typeId) AND " +
           "(:contactPattern IS NULL OR LOWER(c.contact) LIKE :contactPattern) AND " +
           "(:actif IS NULL OR c.actif = :actif)")
    List<Client> filtrerClients(
            @Param("id") Long id,
            @Param("nomPattern") String nomPattern,
            @Param("typeId") Long typeId,
            @Param("contactPattern") String contactPattern,
            @Param("actif") Boolean actif);
}
