package mg.itu.aquanova.achat.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.dto.HistoriqueAchatDepenseDTO;
import mg.itu.aquanova.achat.dto.HistoriqueAchatDepenseFilter;
import mg.itu.aquanova.achat.dto.TypeOperation;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Depense;
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

    public List<HistoriqueAchatDepenseDTO> rechercher(HistoriqueAchatDepenseFilter filter) {
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
            var predicates = cb.conjunction();

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
