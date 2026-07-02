package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.dto.IntrantFilter;
import mg.itu.aquanova.achat.models.Intrant;
import mg.itu.aquanova.achat.repositories.IntrantRepository;
import mg.itu.aquanova.achat.repositories.MouvementStockIntrantRepository;

@Service
public class IntrantService {

    private final IntrantRepository repository;
    private final MouvementStockIntrantRepository mouvementRepository;

    public IntrantService(IntrantRepository repository, MouvementStockIntrantRepository mouvementRepository) {
        this.repository = repository;
        this.mouvementRepository = mouvementRepository;
    }

    public Page<Intrant> lister(IntrantFilter filter, Pageable pageable) {
        return repository.findAll(specification(filter), pageable);
    }

    public List<Intrant> listerActifs() {
        return repository.findByActifTrueOrderByNomAsc();
    }

    public List<Intrant> listerTous() {
        return repository.findAllByOrderByNomAsc();
    }

    public Intrant trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intrant introuvable : " + id));
    }

    @Transactional
    public Intrant creer(Intrant intrant) {
        valider(intrant, null);
        normaliser(intrant);
        return repository.save(intrant);
    }

    @Transactional
    public Intrant modifier(Long id, Intrant intrant) {
        Intrant existant = trouverParId(id);
        valider(intrant, id);

        existant.setNom(intrant.getNom());
        existant.setCategorieIntrant(intrant.getCategorieIntrant());
        existant.setUnite(intrant.getUnite());
        existant.setPrixReference(intrant.getPrixReference());
        existant.setDescription(intrant.getDescription());
        existant.setActif(intrant.getActif() != null ? intrant.getActif() : Boolean.TRUE);

        normaliser(existant);
        return repository.save(existant);
    }

    @Transactional
    public void supprimerOuDesactiver(Long id) {
        Intrant intrant = trouverParId(id);
        if (mouvementRepository.countByIntrantId(id) > 0) {
            intrant.setActif(false);
            repository.save(intrant);
        } else {
            repository.delete(intrant);
        }
    }

    private Specification<Intrant> specification(IntrantFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getNom() != null && !filter.getNom().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nom")), "%" + filter.getNom().trim().toLowerCase() + "%"));
            }
            if (filter.getCategorieIntrant() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categorieIntrant"), filter.getCategorieIntrant()));
            }
            if (filter.getUnite() != null && !filter.getUnite().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("unite")), "%" + filter.getUnite().trim().toLowerCase() + "%"));
            }
            if (filter.getActif() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("actif"), filter.getActif()));
            }

            return predicates;
        };
    }

    private void valider(Intrant intrant, Long idIgnore) {
        if (intrant == null) {
            throw new IllegalArgumentException("L'intrant est obligatoire.");
        }
        if (intrant.getNom() == null || intrant.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'intrant est obligatoire.");
        }
        if (intrant.getCategorieIntrant() == null) {
            throw new IllegalArgumentException("La catégorie de l'intrant est obligatoire.");
        }
        if (intrant.getUnite() == null || intrant.getUnite().trim().isEmpty()) {
            throw new IllegalArgumentException("L'unité de l'intrant est obligatoire.");
        }
        if (intrant.getPrixReference() != null && intrant.getPrixReference().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix de référence ne peut pas être négatif.");
        }
    }

    private void normaliser(Intrant intrant) {
        intrant.setNom(intrant.getNom().trim());
        intrant.setUnite(intrant.getUnite().trim());
        if (intrant.getActif() == null) {
            intrant.setActif(true);
        }
    }
}
