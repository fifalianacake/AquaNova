package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import mg.itu.aquanova.achat.dto.DepenseFilter;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.models.DepensePaiement;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.DepenseRepository;

@Service
public class DepenseService {

    private final DepenseRepository depenseRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;

    public DepenseService(
            DepenseRepository depenseRepository,
            CategorieDepenseRepository categorieDepenseRepository) {
        this.depenseRepository = depenseRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
    }

    public Page<Depense> lister(DepenseFilter filter, Pageable pageable) {
        return depenseRepository.findAll(specification(filter), pageable);
    }

    public List<Depense> listerPourExport(DepenseFilter filter) {
        return depenseRepository.findAll(specification(filter));
    }

    public Depense trouverParId(Long id) {
        return depenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dépense introuvable : " + id));
    }

    @Transactional
    public Depense enregistrer(Depense depense) {
        if (depense == null) {
            throw new IllegalArgumentException("La dépense est obligatoire.");
        }
        if (depense.getPaiements() != null && !depense.getPaiements().isEmpty()) {
            depense.reglerTotalPaiements();
        }

        valider(depense);
        normaliser(depense);
        normaliserPaiements(depense);
        return depenseRepository.save(depense);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!depenseRepository.existsById(id)) {
            throw new EntityNotFoundException("Dépense introuvable : " + id);
        }
        try {
            depenseRepository.deleteById(id);
            depenseRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            // Cas typique : la dépense a été générée par la clôture d'une intervention de
            // maintenance, qui la référence. On refuse la suppression orpheline plutôt que
            // de laisser remonter une erreur d'intégrité brute.
            throw new IllegalStateException(
                    "Suppression refusée : cette dépense est rattachée à une autre opération (par exemple une intervention de maintenance).");
        }
    }

    public BigDecimal calculerTotalFiltre(DepenseFilter filter) {
        return listerPourExport(filter).stream()
                .map(depense -> depense.getMontant())
                .filter(montant -> montant != null)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    private Specification<Depense> specification(DepenseFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getCategorieDepenseId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categorieDepense").get("id"), filter.getCategorieDepenseId()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateDepense"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateDepense"), filter.getDateFin()));
            }
            if (filter.getMontantMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("montant"), filter.getMontantMin()));
            }
            if (filter.getMontantMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("montant"), filter.getMontantMax()));
            }
            if (filter.getModePaiement() != null && !filter.getModePaiement().isBlank()) {
                var paiements = root.join("paiements", JoinType.LEFT);
                predicates = cb.and(predicates, cb.like(cb.lower(paiements.get("modePaiement").as(String.class)), "%" + filter.getModePaiement().trim().toLowerCase() + "%"));
            }

            return predicates;
        };
    }

    private void valider(Depense depense) {
        if (depense == null) {
            throw new IllegalArgumentException("La dépense est obligatoire.");
        }
        if (depense.getDateDepense() == null) {
            throw new IllegalArgumentException("La date est obligatoire.");
        }
        if (depense.getCategorieDepense() == null || depense.getCategorieDepense().getId() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire.");
        }
        if (depense.getLibelle() == null || depense.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire.");
        }

        if (depense.getPaiements() == null || depense.getPaiements().isEmpty()) {
            if (depense.getMontant() == null || depense.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant doit être strictement supérieur à zéro.");
            }
        } else {
            boolean anyPayment = false;
            for (DepensePaiement paiement : depense.getPaiements()) {
                if (paiement == null) {
                    continue;
                }
                if (paiement.getModePaiement() == null) {
                    throw new IllegalArgumentException("Le mode de paiement de chaque ligne est obligatoire.");
                }
                if (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Le montant de chaque ligne de paiement doit être supérieur à zéro.");
                }
                anyPayment = true;
            }
            if (!anyPayment) {
                throw new IllegalArgumentException("Au moins une ligne de paiement valide est requise.");
            }
        }
    }

    private void normaliser(Depense depense) {
        CategorieDepense categorie = categorieDepenseRepository.findById(depense.getCategorieDepense().getId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + depense.getCategorieDepense().getId()));

        depense.setCategorieDepense(categorie);
        depense.setLibelle(depense.getLibelle().trim());
        depense.setReference(blankToNull(depense.getReference()));
        depense.setObservation(blankToNull(depense.getObservation()));

        if (depense.getPaiements() != null && !depense.getPaiements().isEmpty()) {
            depense.setModePaiement(null);
        } else {
            depense.setModePaiement(blankToNull(depense.getModePaiement()));
        }
    }

    private void normaliserPaiements(Depense depense) {
        if (depense.getPaiements() == null) {
            return;
        }
        depense.getPaiements().removeIf(paiement -> paiement == null || montantVide(paiement));
        for (DepensePaiement paiement : depense.getPaiements()) {
            paiement.setDepense(depense);
            paiement.setReference(blankToNull(paiement.getReference()));
            paiement.setObservation(blankToNull(paiement.getObservation()));
        }
    }

    private boolean montantVide(DepensePaiement paiement) {
        return paiement.getModePaiement() == null
                && (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) == 0)
                && (paiement.getReference() == null || paiement.getReference().isBlank())
                && (paiement.getObservation() == null || paiement.getObservation().isBlank());
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
