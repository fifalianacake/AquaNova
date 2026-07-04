package mg.itu.aquanova.achat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.achat.models.Achat;

public interface AchatRepository extends JpaRepository<Achat, Long>, JpaSpecificationExecutor<Achat> {
    List<Achat> findByFournisseurId(Long fournisseurId);
}
