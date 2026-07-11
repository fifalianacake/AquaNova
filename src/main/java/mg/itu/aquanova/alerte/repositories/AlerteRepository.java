package mg.itu.aquanova.alerte.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Bassin;

public interface AlerteRepository extends JpaRepository<Alerte, Long>, JpaSpecificationExecutor<Alerte> {

    List<Alerte> findByStatutNotInOrderByDateCreationDesc(List<StatutAlerte> statutsExclus);

    List<Alerte> findByNiveauCriticiteAndStatutNotInOrderByDateCreationDesc(
            NiveauCriticite niveauCriticite, List<StatutAlerte> statutsExclus);

    long countByNiveauCriticiteAndStatutNotIn(NiveauCriticite niveauCriticite, List<StatutAlerte> statutsExclus);

    Optional<Alerte> findFirstByModuleSourceAndTypeAlerteAndLotAndBassinAndStatut(
        ModuleSource moduleSource,
        TypeAlerte typeAlerte,
        LotModels lot,
        Bassin bassin,
        StatutAlerte statut
    );
}
