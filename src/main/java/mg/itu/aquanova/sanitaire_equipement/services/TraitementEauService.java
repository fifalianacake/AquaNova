package mg.itu.aquanova.production.services;

import mg.itu.aquanova.sanitaire_equipement.models.TraitementEau;
import mg.itu.aquanova.sanitaire_equipement.repositories.TraitementEauRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TraitementEauService {
    private final TraitementEauRepository repository;

    public TraitementEauService(TraitementEauRepository repository) { this.repository = repository; }

    @Transactional
    public TraitementEau create(TraitementEau traitement) {
        if (traitement.getBassin() == null || traitement.getBassin().getId() == null) {
            throw new RuntimeException("Le bassin est obligatoire");
        }
        if (traitement.getTypeTraitementEau() == null || traitement.getTypeTraitementEau().getId() == null) {
            throw new RuntimeException("Le type de traitement est obligatoire");
        }
        if (traitement.getDateTraitement() == null) {
            throw new RuntimeException("La date de traitement est obligatoire");
        }
        if (traitement.getDetail() == null || traitement.getDetail().trim().isEmpty()) {
            throw new RuntimeException("Le détail du traitement est obligatoire");
        }
        return repository.save(traitement);
    }

    public List<TraitementEau> search(Long id, Long bassinId, Long typeId, LocalDate debut, LocalDate fin) {
        return repository.filtrerTraitements(id, bassinId, typeId, debut, fin);
    }

    public TraitementEau trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Traitement introuvable"));
    }

    @Transactional
    public void delete(Long id) { repository.deleteById(id); }
}