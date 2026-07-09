package mg.itu.aquanova.sanitaire_equipement.repositories;

import mg.itu.aquanova.sanitaire_equipement.models.Maintenance;
import mg.itu.aquanova.sanitaire_equipement.models.StatutInterventionEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long>, JpaSpecificationExecutor<Maintenance> {
    List<Maintenance> findByEquipementId(Long idEquipement);
    List<Maintenance> findByStatutIntervention(StatutInterventionEnum statut);
    List<Maintenance> findByCategorieMaintenanceId(Long idCategorie);
    List<Maintenance> findByStatutInterventionAndCategorieMaintenanceId(StatutInterventionEnum statut, Long idCategorie);
    List<Maintenance> findByEquipementIdAndStatutInterventionIn(Long idEquipement, Collection<StatutInterventionEnum> statuts);
    boolean existsByEquipementId(Long id);
}