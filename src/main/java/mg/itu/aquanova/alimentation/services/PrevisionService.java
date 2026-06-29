package mg.itu.aquanova.alimentation.services;


import mg.itu.aquanova.alimentation.models.Aliment;
import mg.itu.aquanova.alimentation.models.PrevisionResult;
import mg.itu.aquanova.alimentation.repositories.AlimentRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class PrevisionService {

    private final AlimentRepository alimentRepository;
    private final DistributionRepository distributionRepository;
    private final StockService stockService;

    public PrevisionService(AlimentRepository alimentRepository,
                            DistributionRepository distributionRepository,
                            StockService stockService) {
        this.alimentRepository = alimentRepository;
        this.distributionRepository = distributionRepository;
        this.stockService = stockService;
    }

    public BigDecimal calculateConsumption(Long alimentId) {
        BigDecimal totalDistribue = this.distributionRepository
                .sumQuantiteByAlimentId(alimentId);
        Long joursDistincts = this.distributionRepository
                .countDistinctJoursByAlimentId(alimentId);

        if (joursDistincts == null || joursDistincts == 0) {
            return BigDecimal.ZERO;
        }

        return totalDistribue.divide(
                BigDecimal.valueOf(joursDistincts),
                2,
                RoundingMode.HALF_UP
        );
    }

    public LocalDate calculateRuptureDate(Long alimentId) {
        BigDecimal stock = this.stockService.getStockAtDate(alimentId, LocalDate.now());
        BigDecimal consoJour = this.calculateConsumption(alimentId);

        if (consoJour.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        long joursRestants = stock
                .divide(consoJour, 0, RoundingMode.FLOOR)
                .longValue();

        return LocalDate.now().plusDays(joursRestants);
    }

    public BigDecimal suggestPurchase(Long alimentId) {
        BigDecimal consoJour = this.calculateConsumption(alimentId);
        BigDecimal stock = this.stockService.getStockAtDate(alimentId, LocalDate.now());

        if (consoJour.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal stockIdeal = consoJour.multiply(BigDecimal.valueOf(30));
        BigDecimal suggestion = stockIdeal.subtract(stock);

        return suggestion.compareTo(BigDecimal.ZERO) > 0
                ? suggestion.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public List<PrevisionResult> getAllPrevisions() {
        List<Aliment> aliments = this.alimentRepository.findAll();
        List<PrevisionResult> results = new ArrayList<>();

        for (Aliment aliment : aliments) {
            BigDecimal stock = this.stockService.getStockAtDate(
                    aliment.getId(), LocalDate.now());
            BigDecimal consoJour = this.calculateConsumption(aliment.getId());
            LocalDate dateRupture = this.calculateRuptureDate(aliment.getId());
            BigDecimal suggestion = this.suggestPurchase(aliment.getId());

            Long joursRestants = null;
            if (consoJour.compareTo(BigDecimal.ZERO) > 0) {
                joursRestants = stock
                        .divide(consoJour, 0, RoundingMode.FLOOR)
                        .longValue();
            }

            PrevisionResult result = new PrevisionResult();
            result.setAlimentId(aliment.getId());
            result.setAlimentNom(aliment.getNom());
            result.setStockRestant(stock);
            result.setConsommationParJour(consoJour);
            result.setJoursRestants(joursRestants);
            result.setDateRupture(dateRupture);
            result.setQuantiteSuggereePurchase(suggestion);

            results.add(result);
        }

        return results;
    }
}