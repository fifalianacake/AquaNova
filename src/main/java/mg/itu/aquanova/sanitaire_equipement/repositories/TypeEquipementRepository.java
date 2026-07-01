package mg.itu.aquanova.sanitaire_equipement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.sanitaire_equipement.models.TypeEquipement;

@Repository
public interface TypeEquipementRepository extends JpaRepository<TypeEquipement, Long> {
}