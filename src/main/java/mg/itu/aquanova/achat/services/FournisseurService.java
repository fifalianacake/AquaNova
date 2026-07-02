package mg.itu.aquanova.achat.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;

@Service
public class FournisseurService {

    private final FournisseurRepository repository;

    public FournisseurService(FournisseurRepository repository) {
        this.repository = repository;
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
}
