package mg.itu.aquanova.sanitaire_equipement.services;

import mg.itu.aquanova.sanitaire_equipement.dto.TraitementEauFilter;
import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import mg.itu.aquanova.sanitaire_equipement.repositories.TraitementEauRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class TraitementEauService {
    private final TraitementEauRepository repository;

    public TraitementEauService(TraitementEauRepository repository) { this.repository = repository; }

    @Transactional
    public TraitementEau create(TraitementEau traitement) {
        validate(traitement);
        return repository.save(traitement);
    }

    @Transactional
    public TraitementEau update(Long id, TraitementEau traitement) {
        TraitementEau existing = trouverParId(id);
        validate(traitement);

        existing.setBassin(traitement.getBassin());
        existing.setTypeTraitementEau(traitement.getTypeTraitementEau());
        existing.setDateTraitement(traitement.getDateTraitement());
        existing.setDetail(traitement.getDetail());
        existing.setObservation(traitement.getObservation());

        if (traitement.getUtilisateur() != null) {
            existing.setUtilisateur(traitement.getUtilisateur());
        }

        return repository.save(existing);
    }

    private void validate(TraitementEau traitement) {
        if (traitement.getBassin() == null || traitement.getBassin().getId() == null) {
            throw new RuntimeException("Le bassin est obligatoire");
        }
        if (traitement.getTypeTraitementEau() == null || traitement.getTypeTraitementEau().getId() == null) {
            throw new RuntimeException("Le type de traitement est obligatoire");
        }
        if (traitement.getDateTraitement() == null) {
            throw new RuntimeException("La date de traitement est obligatoire");
        }
        if (traitement.getDetail() == null || traitement.getDetail().trim().isEmpty()) {
            throw new RuntimeException("Le détail du traitement est obligatoire");
        }
    }

    public List<TraitementEau> lister(TraitementEauFilter filter) {
        return repository.findAll(specification(filter), Sort.by(Sort.Direction.DESC, "dateTraitement"));
    }

    private Specification<TraitementEau> specification(TraitementEauFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getBassinId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bassin").get("id"), filter.getBassinId()));
            }
            if (filter.getTypeId() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("typeTraitementEau").get("id"), filter.getTypeId()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("dateTraitement"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("dateTraitement"), filter.getDateFin()));
            }

            return predicates;
        };
    }

    public TraitementEau trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Traitement introuvable"));
    }

    @Transactional
    public void delete(Long id) { repository.deleteById(id); }
}
