package mg.itu.aquanova.sanitaire_equipement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mg.itu.aquanova.sanitaire_equipement.models.StatutEquipement;

import mg.itu.aquanova.sanitaire_equipement.models.Equipement;

import java.util.List;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByBassinId(Long idBassin);
    List<Equipement> findByStatut(StatutEquipement statut);
}