package mg.itu.aquanova.achat.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Intrant;
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

    public CategorieDepense create(CategorieDepense cat) {
        try {
            return repository.save(cat);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la catégorie", e);
        }
    }

    public CategorieDepense trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + id));
    }

    public CategorieDepense trouverParCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + code));
    }

    @Transactional
    public CategorieDepense modifier(Long id, CategorieDepense categorie) {
        CategorieDepense existant = trouverParId(id);

        existant.setCode(categorie.getCode());
        existant.setLibelle(categorie.getLibelle());
        existant.setDescription(categorie.getDescription());
        existant.setTypeCategorie(categorie.getTypeCategorie());
        
        normaliser(existant);
        return repository.save(existant);
    }

    public void delete(Long id) {
        CategorieDepense cat = trouverParId(id);
        repository.delete(cat);
    }

    public boolean estCategorieAchatIntrant(CategorieDepense categorie) {
        return categorie != null && categorie.getCode() != null && CODES_ACHAT_INTRANTS.contains(categorie.getCode());
    }

    public boolean estCategorieAchatAlevin(CategorieDepense categorie) {
        return categorie != null && categorie.getCode() != null && categorie.getCode().equals("ACHAT_ALEVINS");
    }

    public void normaliser(CategorieDepense categorie) {
        categorie.setCode(categorie.getCode().toUpperCase().trim());
        categorie.setLibelle(categorie.getLibelle().trim());
        categorie.setDescription(categorie.getDescription().trim());
    }
}
