package mg.itu.aquanova.sanitaireequipement.repositories;

import mg.itu.aquanova.sanitaireequipement.models.TypeEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeEquipementRepository extends JpaRepository<TypeEquipement, Long> {
}