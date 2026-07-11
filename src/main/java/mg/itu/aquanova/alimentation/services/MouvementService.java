package mg.itu.aquanova.alimentation.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.alerte.services.AnalyseVerificationService;
import mg.itu.aquanova.alimentation.dto.MouvementFilter;
import mg.itu.aquanova.alimentation.models.*;
import mg.itu.aquanova.alimentation.repositories.MouvementStockRepository;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class MouvementService {

    @Autowired
    private MouvementStockRepository repo;

    @Autowired
    private AlimentRepository alimentRepository;

    @Autowired
    private AnalyseVerificationService analyseVerificationService;

    public MouvementStock create(MouvementStock m) {

        validate(m);

        List<MouvementStock> all = repo.findByAlimentId(m.getAliment().getId());

        all.add(m);

        validateTimelineList(all);

        MouvementStock sauvegarde = repo.save(m);
        verifierStockApresMouvement(sauvegarde.getAliment());
        return sauvegarde;
    }

    private void verifierStockApresMouvement(Aliment aliment) {
        if (aliment == null || aliment.getId() == null) {
            return;
        }
        analyseVerificationService.verifierStockAliment(aliment, getStock(aliment.getId()));
    }

    public Double getStockDisponibleADate(Long alimentId, LocalDate date) {
        return repo.findByAlimentId(alimentId)
                .stream()
                .filter(x -> !x.getDateMouvement().isAfter(date))
                .mapToDouble(x -> {
                    if (x.getTypeMouvement() == TypeMouvement.ENTREE)
                        return x.getQuantite();
                    else
                        return -x.getQuantite();
                })
                .sum();
    }

    private void validateTimelineList(List<MouvementStock> list) {

        list = list.stream()
                .sorted(Comparator.comparing(MouvementStock::getDateMouvement))
                .toList();

        double stock = 0;

        for (MouvementStock m : list) {

            if (m.getTypeMouvement() == TypeMouvement.ENTREE)
                stock += m.getQuantite();
            else
                stock -= m.getQuantite();

            if (stock < 0) {
                throw new RuntimeException(
                        "Stock devient négatif à la date "
                                + m.getDateMouvement());
            }
        }
    }

    public MouvementStock update(MouvementStock m) {

        validate(m);
        validateUpdate(m);

        MouvementStock existing = findById(m.getId());
        if (existing.getDistribution() != null) {
            throw new IllegalStateException(
                    "Ce mouvement provient de la distribution #" + existing.getDistribution().getId()
                            + " : modifiez cette distribution plutôt que le mouvement directement.");
        }

        List<MouvementStock> all = repo.findByAlimentId(m.getAliment().getId());

        // replace the edited movement in memory
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(m.getId())) {
                all.set(i, m);
                break;
            }
        }

        validateTimelineList(all);

        return repo.save(m);
    }

    public void delete(Long id) {

        MouvementStock toDelete = findById(id);

        if (toDelete.getDistribution() != null) {
            throw new IllegalStateException(
                    "Ce mouvement provient de la distribution #" + toDelete.getDistribution().getId()
                            + " : supprimez cette distribution plutôt que le mouvement directement.");
        }

        deleteInternal(toDelete);
    }

    public void deleteLinkedToDistribution(Long id) {
        deleteInternal(findById(id));
    }

    private void deleteInternal(MouvementStock toDelete) {

        List<MouvementStock> all = repo.findByAlimentId(toDelete.getAliment().getId());

        all.removeIf(m -> m.getId().equals(toDelete.getId()));

        validateTimelineList(all);

        repo.deleteById(toDelete.getId());
    }

    public Page<MouvementStock> lister(MouvementFilter filter, Pageable pageable) {
        return repo.findAll(specification(filter), pageable);
    }

    private Specification<MouvementStock> specification(MouvementFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("typeMouvement"), filter.getType()));
            }
            if (filter.getAlimentId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("aliment").get("id"), filter.getAlimentId()));
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

    public Double getStock(Long alimentId) {

        Double stock = 0.0;

        for (MouvementStock m : repo.findByAlimentId(alimentId)) {

            if (m.getTypeMouvement() == TypeMouvement.ENTREE)
                stock += m.getQuantite();
            else
                stock -= m.getQuantite();
        }

        return stock;
    }

    private void validate(MouvementStock m) {

        if (m == null) {
            throw new IllegalArgumentException("Le mouvement est obligatoire");
        }

        if (m.getDateMouvement() == null) {
            throw new IllegalArgumentException("La date du mouvement est obligatoire");
        }

        if (m.getAliment() == null || m.getAliment().getId() == null) {
            throw new IllegalArgumentException("L'aliment est obligatoire");
        }

        if (!alimentRepository.existsById(m.getAliment().getId())) {
            throw new IllegalArgumentException("Aliment introuvable : " + m.getAliment().getId());
        }

        if (m.getTypeMouvement() == null) {
            throw new IllegalArgumentException("Le type de mouvement est obligatoire");
        }

        if (m.getQuantite() == null || m.getQuantite() <= 0
                || m.getQuantite().isNaN() || m.getQuantite().isInfinite()) {
            throw new IllegalArgumentException("Quantité invalide");
        }
    }

    private void validateUpdate(MouvementStock m) {
        if (m.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du mouvement est obligatoire pour la modification");
        }

        if (!repo.existsById(m.getId())) {
            throw new IllegalArgumentException("Mouvement introuvable : " + m.getId());
        }
    }

    public MouvementStock findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }

    public java.util.Optional<MouvementStock> findByDistributionId(Long distributionId) {
        return repo.findByDistributionId(distributionId);
    }
}
