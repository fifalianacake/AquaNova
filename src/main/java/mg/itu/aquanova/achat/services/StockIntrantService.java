package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.JoinType;
import mg.itu.aquanova.achat.dto.IntrantFilter;
import mg.itu.aquanova.achat.dto.MouvementStockIntrantFilter;
import mg.itu.aquanova.achat.dto.StockIntrantDTO;
import mg.itu.aquanova.achat.models.MouvementStockIntrant;
import mg.itu.aquanova.achat.models.TypeMouvementIntrant;
import mg.itu.aquanova.achat.repositories.MouvementStockIntrantRepository;

@Service
public class StockIntrantService {

    private final IntrantService intrantService;
    private final MouvementStockIntrantRepository mouvementRepository;

    public StockIntrantService(IntrantService intrantService, MouvementStockIntrantRepository mouvementRepository) {
        this.intrantService = intrantService;
        this.mouvementRepository = mouvementRepository;
    }

    public Page<StockIntrantDTO> listerStocks(IntrantFilter filter, Pageable pageable) {
        return intrantService.lister(filter, pageable)
                .map(intrant -> new StockIntrantDTO(intrant, calculerStock(intrant.getId())));
    }

    public BigDecimal calculerStock(Long intrantId) {
        MouvementStockIntrantFilter filter = new MouvementStockIntrantFilter();
        filter.setIntrantId(intrantId);
        return mouvementRepository.findAll(specificationMouvements(filter)).stream()
                .map(this::quantiteSignee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Page<MouvementStockIntrant> listerMouvements(MouvementStockIntrantFilter filter, Pageable pageable) {
        return mouvementRepository.findAll(specificationMouvements(filter), pageable);
    }

    private Specification<MouvementStockIntrant> specificationMouvements(MouvementStockIntrantFilter filter) {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("intrant", JoinType.LEFT);
            }

            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getIntrantId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("intrant").get("id"), filter.getIntrantId()));
            }
            if (filter.getTypeMouvement() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("typeMouvement"), filter.getTypeMouvement()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateMouvement"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateMouvement"), filter.getDateFin()));
            }

            return predicates;
        };
    }

    private BigDecimal quantiteSignee(MouvementStockIntrant mouvement) {
        if (mouvement.getQuantite() == null) {
            return BigDecimal.ZERO;
        }
        if (mouvement.getTypeMouvement() == TypeMouvementIntrant.ENTREE) {
            return mouvement.getQuantite();
        }
        return mouvement.getQuantite().negate();
    }
}
