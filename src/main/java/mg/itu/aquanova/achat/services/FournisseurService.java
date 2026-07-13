package mg.itu.aquanova.achat.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;

@Service
public class FournisseurService {

    private final FournisseurRepository repository;
    private final AchatRepository achatRepository;

    public FournisseurService(FournisseurRepository repository, AchatRepository achatRepository) {
        this.repository = repository;
        this.achatRepository = achatRepository;
    }

    public List<Fournisseur> listerTous() {
        return repository.findAllByOrderByNomAsc();
    }

    public List<Fournisseur> listerActifs() {
        return repository.findByActifTrueOrderByNomAsc();
    }

    public Fournisseur trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + id));
    }

    public Fournisseur sauvegarder(Fournisseur fournisseur) {
        return repository.save(fournisseur);
    }

    public void desactiver(Long id) {
        Fournisseur fournisseur = trouverParId(id);
        fournisseur.setActif(false);
        repository.save(fournisseur);
    }

    public List<Fournisseur> rechercherAvecFiltres(Long id, String nom, String typeFournisseur, String contact, Boolean actif) {
        if (id == null && (nom == null || nom.isEmpty()) && (typeFournisseur == null || typeFournisseur.isEmpty()) 
                && (contact == null || contact.isEmpty()) && actif == null) {
            return listerTous();
        }

        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (nom != null && !nom.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%"));
            }
            if (typeFournisseur != null && !typeFournisseur.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("typeFournisseur").as(String.class), typeFournisseur));
            }
            if (contact != null && !contact.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("contact")), "%" + contact.toLowerCase() + "%"));
            }
            if (actif != null) {
                predicates.add(cb.equal(root.get("actif"), actif));
            }
            query.orderBy(cb.asc(root.get("nom")));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    public List<Achat> listerAchatsParFournisseur(Long fournisseurId) {
        return achatRepository.findByFournisseurId(fournisseurId);
    }
}
