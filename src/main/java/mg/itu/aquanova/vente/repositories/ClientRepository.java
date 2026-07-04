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
           "(:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:typeId IS NULL OR c.typeClient.id = :typeId) AND " +
           "(:contact IS NULL OR c.contact LIKE CONCAT('%', :contact, '%')) AND " +
           "(:actif IS NULL OR c.actif = :actif)")
    List<Client> filtrerClients(
            @Param("id") Long id,
            @Param("nom") String nom,
            @Param("typeId") Long typeId,
            @Param("contact") String contact,
            @Param("actif") Boolean actif);
}