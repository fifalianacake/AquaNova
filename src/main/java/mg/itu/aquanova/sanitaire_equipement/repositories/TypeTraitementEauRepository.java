package mg.itu.aquanova.sanitaire_equipement.repositories;

import mg.itu.aquanova.sanitaire_equipement.models.TypeTraitementEau;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeTraitementEauRepository extends JpaRepository<TypeTraitementEau, Long> {
}