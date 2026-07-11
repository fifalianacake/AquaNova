package mg.itu.aquanova.achat.services;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.models.Achat;
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

    /** Une catégorie de dépense référencée par un achat ne peut pas être supprimée. */
    public boolean estDejaUtilise(Long id) {
        return achatRepository.existsByCategorieDepenseId(id);
    }
}
