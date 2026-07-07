package mg.itu.aquanova.sanitaire_equipement.repositories;

import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenance;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenanceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieMaintenanceRepository extends JpaRepository<CategorieMaintenance, Long> {
    boolean existsByLibelle(CategorieMaintenanceEnum libelle);
}
