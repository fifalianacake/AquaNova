package mg.itu.aquanova.referentiel.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;

@Repository
public interface StatutBassinRepository extends JpaRepository<StatutBassin, Long> {
    Optional<StatutBassin> findByLibelle(LibelleStatutBassin libelle);

    boolean existsByLibelle(LibelleStatutBassin libelle);
}
