package mg.itu.aquanova.achat.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.dto.HistoriqueAchatDepenseDTO;
import mg.itu.aquanova.achat.dto.HistoriqueAchatDepenseFilter;
import mg.itu.aquanova.achat.dto.TypeOperation;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.DepenseRepository;

@Service
public class HistoriqueAchatDepenseService {

    private final AchatRepository achatRepository;
    private final DepenseRepository depenseRepository;

    public HistoriqueAchatDepenseService(AchatRepository achatRepository, DepenseRepository depenseRepository) {
        this.achatRepository = achatRepository;
        this.depenseRepository = depenseRepository;
    }

    /**
     * Liste paginée de l'historique achats/dépenses.
     *
     * Particularité assumée : cet historique résulte d'une UNION en mémoire de deux types
     * d'entités distinctes (Achat et Depense), chacune récupérée via sa propre Specification.
     * Il n'existe pas de requête JPA unique capable de paginer nativement ce résultat combiné
     * (pas d'entité commune, pas de vue SQL). On construit donc la liste complète triée par
     * date décroissante, puis on la découpe manuellement selon le Pageable reçu avant de
     * l'envelopper dans un PageImpl. C'est le seul endroit du module achat_depense où cette
     * approche "in-memory" reste légitime (comme le faisaient Lots/Maintenance avant leur
     * propre migration vers Pageable natif) : partout ailleurs, on pagine directement via
     * JpaSpecificationExecutor.
     */
    public Page<HistoriqueAchatDepenseDTO> lister(HistoriqueAchatDepenseFilter filter, Pageable pageable) {
        List<HistoriqueAchatDepenseDTO> tout = construireListeTriee(filter);

        int total = tout.size();
        int debut = (int) pageable.getOffset();
        if (debut > total) {
            debut = total;
        }
        int fin = Math.min(debut + pageable.getPageSize(), total);

        List<HistoriqueAchatDepenseDTO> contenuPage = debut >= fin ? List.of() : tout.subList(debut, fin);
        return new PageImpl<>(contenuPage, pageable, total);
    }

    private List<HistoriqueAchatDepenseDTO> construireListeTriee(HistoriqueAchatDepenseFilter filter) {
        List<HistoriqueAchatDepenseDTO> result = new ArrayList<>();

        if (filter == null || filter.getTypeOperation() == null || filter.getTypeOperation() == TypeOperation.ACHAT) {
            result.addAll(rechercherAchats(filter));
        }
        if (filter == null || filter.getTypeOperation() == null || filter.getTypeOperation() == TypeOperation.DEPENSE) {
            result.addAll(rechercherDepenses(filter));
        }

        result.sort((first, second) -> {
            if (first.getDate() == null && second.getDate() == null) {
                return 0;
            }
            if (first.getDate() == null) {
                return 1;
            }
            if (second.getDate() == null) {
                return -1;
            }
            return second.getDate().compareTo(first.getDate());
        });
        return result;
    }

    private List<HistoriqueAchatDepenseDTO> rechercherAchats(HistoriqueAchatDepenseFilter filter) {
        return achatRepository.findAll(specificationAchats(filter)).stream()
                .map(this::convertAchat)
                .toList();
    }

    private List<HistoriqueAchatDepenseDTO> rechercherDepenses(HistoriqueAchatDepenseFilter filter) {
        return depenseRepository.findAll(specificationDepenses(filter)).stream()
                .map(this::convertDepense)
                .toList();
    }

    private Specification<Achat> specificationAchats(HistoriqueAchatDepenseFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.equal(root.get("statutAchat"), StatutAchat.VALIDE);

            if (filter == null) {
                return predicates;
            }
            if (filter.getFournisseurId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("fournisseur").get("id"), filter.getFournisseurId()));
            }
            if (filter.getCategorieDepenseId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categorieDepense").get("id"), filter.getCategorieDepenseId()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateAchat"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateAchat"), filter.getDateFin()));
            }
            if (filter.getMontantMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("montantTotal"), filter.getMontantMin()));
            }
            if (filter.getMontantMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("montantTotal"), filter.getMontantMax()));
            }
            return predicates;
        };
    }

    private Specification<Depense> specificationDepenses(HistoriqueAchatDepenseFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
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
            return predicates;
        };
    }

    private HistoriqueAchatDepenseDTO convertAchat(Achat achat) {
        HistoriqueAchatDepenseDTO dto = new HistoriqueAchatDepenseDTO();
        dto.setId(achat.getId());
        dto.setTypeOperation(TypeOperation.ACHAT);
        dto.setDate(achat.getDateAchat());
        dto.setFournisseur(achat.getFournisseur() != null ? achat.getFournisseur().getNom() : "");
        dto.setCategorie(achat.getCategorieDepense() != null ? achat.getCategorieDepense().getCode() + " - " + achat.getCategorieDepense().getLibelle() : "");
        dto.setMontant(achat.getMontantTotal());
        dto.setDetails(achat.getReferenceFacture() != null && !achat.getReferenceFacture().isBlank()
                ? achat.getReferenceFacture()
                : achat.getObservation());
        return dto;
    }

    private HistoriqueAchatDepenseDTO convertDepense(Depense depense) {
        HistoriqueAchatDepenseDTO dto = new HistoriqueAchatDepenseDTO();
        dto.setId(depense.getId());
        dto.setTypeOperation(TypeOperation.DEPENSE);
        dto.setDate(depense.getDateDepense());
        dto.setFournisseur("");
        dto.setCategorie(depense.getCategorieDepense() != null ? depense.getCategorieDepense().getCode() + " - " + depense.getCategorieDepense().getLibelle() : "");
        dto.setMontant(depense.getMontant());
        dto.setDetails(depense.getLibelle());
        return dto;
    }
}
