package mg.itu.aquanova.achat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.achat.models.MouvementStockIntrant;
import mg.itu.aquanova.achat.models.TypeMouvementIntrant;

public interface MouvementStockIntrantRepository extends JpaRepository<MouvementStockIntrant, Long>, JpaSpecificationExecutor<MouvementStockIntrant> {
    List<MouvementStockIntrant> findByIntrantIdOrderByDateMouvementDescIdDesc(Long intrantId);
    boolean existsByLigneAchatIdAndTypeMouvement(Long ligneAchatId, TypeMouvementIntrant typeMouvement);
    long countByIntrantId(Long intrantId);
}
