package mg.itu.aquanova.sanitaire_equipement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.sanitaire_equipement.models.ReleveEau;

public interface ReleveEauRepository extends JpaRepository<ReleveEau, Long> {

    List<ReleveEau> findByBassinId(Long bassinId);

}