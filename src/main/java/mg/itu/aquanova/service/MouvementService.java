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


    public MouvementStock create(MouvementStock mouvement) {
        if (mouvement.getQuantiteKg() == null || mouvement.getQuantiteKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }

        if (mouvement.getTypeMouvement() == TypeMouvement.SORTIE
                || mouvement.getTypeMouvement() == TypeMouvement.PERTE) {

            BigDecimal stockDisponible = stockService.getStockByAlimentAndDate(
                    mouvement.getAliment().getId(),
                    mouvement.getDateMouvement());

            if (stockDisponible.compareTo(mouvement.getQuantiteKg()) < 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour l'aliment id=" + mouvement.getAliment().getId()
                                + " à la date " + mouvement.getDateMouvement()
                                + " (disponible: " + stockDisponible + " kg, demandé: " + mouvement.getQuantiteKg()
                                + " kg)");
            }
        }

        return mouvementStockRepository.save(mouvement);
    }

    public Optional<MouvementStock> findById(Long id) {
        return mouvementStockRepository.findById(id);
    }

    public List<MouvementStock> search(Long id, LocalDate dateDebut, LocalDate dateFin,
            Long alimentId, TypeMouvement type) {
        return mouvementStockRepository.search(id, dateDebut, dateFin, alimentId, type);
    }

 
    public List<MouvementStock> getRecentByAliment(Long alimentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mouvementStockRepository.findByAliment_IdOrderByDateMouvementDesc(alimentId, pageable);
    }

   
    public List<MouvementStock> getRecentByAlimentAndDate(Long alimentId, LocalDate date, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return mouvementStockRepository.findRecentByAlimentAndDate(alimentId, date, pageable);
    }

    public List<MouvementStock> getByAlimentAndDate(Long alimentId, LocalDate date) {
        return mouvementStockRepository.search(null, null, date, alimentId, null);
    }

    public void delete(Long id) {
        mouvementStockRepository.deleteById(id);
    }
}