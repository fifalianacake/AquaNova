package mg.itu.aquanova.service;

import mg.itu.aquanova.entity.MouvementStock;
import mg.itu.aquanova.entity.TypeMouvement;
import mg.itu.aquanova.exception.StockInsuffisantException;
import mg.itu.aquanova.repository.MouvementStockRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MouvementService {

    private final MouvementStockRepository mouvementStockRepository;
    private final StockService stockService;

    public MouvementService(MouvementStockRepository mouvementStockRepository,
                             StockService stockService) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.stockService = stockService;
    }

    /**
     * Crée un mouvement de stock après validation des règles métier:
     * - montant (quantite_kg) > 0
     * - si SORTIE ou PERTE, le stock disponible à la date du mouvement doit être suffisant
     *   (aucun stock négatif autorisé)
     */
    public MouvementStock create(MouvementStock mouvement) {
        if (mouvement.getQuantiteKg() == null || mouvement.getQuantiteKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }

        if (mouvement.getTypeMouvement() == TypeMouvement.SORTIE
                || mouvement.getTypeMouvement() == TypeMouvement.PERTE) {

            BigDecimal stockDisponible = stockService.getStockByAlimentAndDate(
                    mouvement.getAliment().getId(),
                    mouvement.getDateMouvement()
            );

            if (stockDisponible.compareTo(mouvement.getQuantiteKg()) < 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour l'aliment id=" + mouvement.getAliment().getId()
                                + " à la date " + mouvement.getDateMouvement()
                                + " (disponible: " + stockDisponible + " kg, demandé: " + mouvement.getQuantiteKg() + " kg)"
                );
            }
        }

        return mouvementStockRepository.save(mouvement);
    }

    public Optional<MouvementStock> findById(Long id) {
        return mouvementStockRepository.findById(id);
    }

    /**
     * Recherche multi-critères pour la page Liste mouvements (/stocks/mouvements).
     * Chaque paramètre est optionnel (null = pas filtré).
     * Filtrage fait en mémoire (findAll() + streams) plutôt qu'en SQL dynamique,
     * pour éviter le problème Postgres "could not determine data type of parameter"
     * rencontré avec une requête JPQL du type (:param IS NULL OR ...).
     */
    public List<MouvementStock> search(Long id, LocalDate dateDebut, LocalDate dateFin,
                                        Long alimentId, TypeMouvement type) {
        return mouvementStockRepository.findAll().stream()
                .filter(m -> id == null || m.getId().equals(id))
                .filter(m -> dateDebut == null || !m.getDateMouvement().isBefore(dateDebut))
                .filter(m -> dateFin == null || !m.getDateMouvement().isAfter(dateFin))
                .filter(m -> alimentId == null || m.getAliment().getId().equals(alimentId))
                .filter(m -> type == null || m.getTypeMouvement() == type)
                .sorted(Comparator.comparing(MouvementStock::getDateMouvement).reversed())
                .toList();
    }

    /**
     * Historique récent des mouvements d'un aliment, sans filtre de date (fiche aliment).
     */
    public List<MouvementStock> getRecentByAliment(Long alimentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mouvementStockRepository.findByAliment_IdOrderByDateMouvementDesc(alimentId, pageable);
    }

    /**
     * Historique récent des mouvements d'un aliment jusqu'à une date donnée (fiche état de stock).
     */
    public List<MouvementStock> getRecentByAlimentAndDate(Long alimentId, LocalDate date, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mouvementStockRepository.findRecentByAlimentAndDate(alimentId, date, pageable);
    }

    /**
     * Récupère les mouvements d'un aliment jusqu'à une date donnée (utilisé par StockController
     * pour la fiche état de stock — équivalent de mouvementService.getByAlimentAndDate dans la liste de tâches).
     */
    public List<MouvementStock> getByAlimentAndDate(Long alimentId, LocalDate date) {
        return search(null, null, date, alimentId, null);
    }

    public void delete(Long id) {
        mouvementStockRepository.deleteById(id);
    }
}