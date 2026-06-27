package mg.itu.aquanova.sanitaireequipement.repositories;

import mg.itu.aquanova.sanitaireequipement.models.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByBassinId(Long idBassin);
    List<Equipement> findByStatut(String statut);
}