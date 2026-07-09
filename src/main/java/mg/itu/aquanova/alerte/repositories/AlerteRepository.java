package mg.itu.aquanova.alerte.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;

public interface AlerteRepository extends JpaRepository<Alerte, Long>, JpaSpecificationExecutor<Alerte> {

    List<Alerte> findByStatutNotInOrderByDateCreationDesc(List<StatutAlerte> statutsExclus);

    List<Alerte> findByNiveauCriticiteAndStatutNotInOrderByDateCreationDesc(
            NiveauCriticite niveauCriticite, List<StatutAlerte> statutsExclus);

    long countByNiveauCriticiteAndStatutNotIn(NiveauCriticite niveauCriticite, List<StatutAlerte> statutsExclus);
}
