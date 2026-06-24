package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.MouvementStock;
import mg.itu.aquanova.production.models.TypeMouvement;
import mg.itu.aquanova.production.repositories.MouvementStockRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class MouvementService {
    private final MouvementStockRepository repository;

    public MouvementService(MouvementStockRepository repository) {
        this.repository = repository;
    }

    public Double calculerStockActuel(Long alimentId, LocalDate dateMax) {
        List<MouvementStock> mvts = repository.filtrerMouvements(null, dateMax, alimentId, null);
        double stock = 0.0;
        for (MouvementStock m : mvts) {
            if (m.getTypeMouvement() == TypeMouvement.ENTREE) {
                stock += m.getQuantite();
            } else {
                stock -= m.getQuantite();
            }
        }
        return stock;
    }

    @Transactional
    public MouvementStock create(MouvementStock mvt) {
        if (mvt.getQuantite() <= 0) {
            throw new RuntimeException("La quantité doit être strictement supérieure à 0");
        }

        if (mvt.getTypeMouvement() == TypeMouvement.SORTIE || mvt.getTypeMouvement() == TypeMouvement.PERTE) {
            Double stockDisponible = calculerStockActuel(mvt.getAliment().getId(), mvt.getDateMouvement());
            if (stockDisponible < mvt.getQuantite()) {
                throw new RuntimeException("Stock insuffisant ! Stock disponible à cette date : " + stockDisponible + " kg");
            }
        }
        return repository.save(mvt);
    }

    public List<MouvementStock> search(LocalDate debut, LocalDate fin, Long alimentId, TypeMouvement typeMvt) {
        return repository.filtrerMouvements(debut, fin, alimentId, typeMvt);
    }

    public List<MouvementStock> getRecentByAliment(Long alimentId) {
        return repository.findTop10ByAlimentIdOrderByDateMouvementDesc(alimentId);
    }

    public MouvementStock trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public MouvementStock update(Long id, MouvementStock mvtDetails) {
        // 1. Récupérer le mouvement existant
        MouvementStock mvtExistant = trouverParId(id);

        // 2. Validation de base sur la nouvelle quantité
        if (mvtDetails.getQuantite() <= 0) {
            throw new RuntimeException("La quantité doit être strictement supérieure à 0");
        }

        // 3. Validation du stock si le type change ou si la quantité augmente
        if (mvtDetails.getTypeMouvement() == TypeMouvement.SORTIE || mvtDetails.getTypeMouvement() == TypeMouvement.PERTE) {
            
            // On recalcule le stock actuel
            Double stockDisponible = calculerStockActuel(mvtDetails.getAliment().getId(), mvtDetails.getDateMouvement());
            
            // Vu qu'on modifie un mouvement existant, il faut rajouter virtuellement l'ancienne quantité 
            // au stock disponible pour ne pas fausser le calcul (annuler l'ancien impact)
            if (mvtExistant.getTypeMouvement() == TypeMouvement.SORTIE || mvtExistant.getTypeMouvement() == TypeMouvement.PERTE) {
                stockDisponible += mvtExistant.getQuantite();
            } else if (mvtExistant.getTypeMouvement() == TypeMouvement.ENTREE) {
                stockDisponible -= mvtExistant.getQuantite(); 
            }

            if (stockDisponible < mvtDetails.getQuantite()) {
                throw new RuntimeException("Stock insuffisant après modification ! Stock disponible simulé : " + stockDisponible + " kg");
            }
        }

        // 4. Mettre à jour les données
        mvtExistant.setDateMouvement(mvtDetails.getDateMouvement());
        mvtExistant.setAliment(mvtDetails.getAliment());
        mvtExistant.setTypeMouvement(mvtDetails.getTypeMouvement());
        mvtExistant.setQuantite(mvtDetails.getQuantite());
        mvtExistant.setCommentaire(mvtDetails.getCommentaire());

        return repository.save(mvtExistant);
    }
}