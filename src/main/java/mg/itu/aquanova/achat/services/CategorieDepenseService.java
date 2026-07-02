package mg.itu.aquanova.achat.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;

@Service
public class CategorieDepenseService {

    public static final Set<String> CODES_ACHAT_INTRANTS = Set.of(
            "ACHAT_MEDICAMENT",
            "PRODUIT_TRAITEMENT",
            "ACHAT_PRODUIT_TRAITEMENT"
    );

    private final CategorieDepenseRepository repository;

    public CategorieDepenseService(CategorieDepenseRepository repository) {
        this.repository = repository;
    }

    public List<CategorieDepense> listerTous() {
        return repository.findAllByOrderByLibelleAsc();
    }

    public List<CategorieDepense> listerCategoriesAchatIntrants() {
        return repository.findByCodeInOrderByLibelleAsc(CODES_ACHAT_INTRANTS);
    }

    public CategorieDepense trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + id));
    }

    public boolean estCategorieAchatIntrant(CategorieDepense categorie) {
        return categorie != null && categorie.getCode() != null && CODES_ACHAT_INTRANTS.contains(categorie.getCode());
    }
}
