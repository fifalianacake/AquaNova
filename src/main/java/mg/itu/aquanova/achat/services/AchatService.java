package mg.itu.aquanova.achat.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;

@Service
public class AchatService {

    private final AchatRepository achatRepository;

    public AchatService(AchatRepository achatRepository) {
        this.achatRepository = achatRepository;
    }

    public Achat trouverParId(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achat introuvable : " + id));
    }

    /** Liste non paginée, utilisée par les exports. Tous statuts confondus par défaut. */
    public List<Achat> listerPourExport(LocalDate dateDebut, LocalDate dateFin, StatutAchat statut) {
        Specification<Achat> specification = (root, query, cb) -> {
            var predicats = cb.conjunction();
            if (dateDebut != null) {
                predicats = cb.and(predicats, cb.greaterThanOrEqualTo(root.get("dateAchat"), dateDebut));
            }
            if (dateFin != null) {
                predicats = cb.and(predicats, cb.lessThanOrEqualTo(root.get("dateAchat"), dateFin));
            }
            if (statut != null) {
                predicats = cb.and(predicats, cb.equal(root.get("statutAchat"), statut));
            }
            return predicats;
        };
        return achatRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "dateAchat"));
    }

    /** Une catégorie de dépense référencée par un achat ne peut pas être supprimée. */
    public boolean estDejaUtilise(Long id) {
        return achatRepository.existsByCategorieDepenseId(id);
    }
}
