package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.alimentation.models.PrevisionResult;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrevisionService {

    private static final int PERIODE_ANALYSE_JOURS = 30;

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

    public BigDecimal calculateConsumption(Long alimentId, LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("dateDebut et dateFin sont obligatoires");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("dateDebut doit être avant dateFin");
        }

        BigDecimal totalDistribue = this.distributionRepository
                .sumQuantiteByAlimentIdAndDateBetween(alimentId, dateDebut, dateFin);

        if (totalDistribue == null) {
            totalDistribue = BigDecimal.ZERO;
        }
        long joursCalendaires = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        if (joursCalendaires <= 0) {
            return BigDecimal.ZERO;
        }
        return totalDistribue.divide(
                BigDecimal.valueOf(joursCalendaires),
                2,
                RoundingMode.HALF_UP
        );
    }

    public LocalDate calculateRuptureDate(LocalDate dateReference, BigDecimal stock, BigDecimal consoJour) {
        if (dateReference == null) {
            throw new IllegalArgumentException("dateReference est obligatoire");
        }
        if (consoJour == null || consoJour.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        long joursRestants = stock
                .divide(consoJour, 0, RoundingMode.FLOOR)
                .longValue();

        return dateReference.plusDays(joursRestants);
    }

    public BigDecimal suggestPurchase(BigDecimal stock, BigDecimal consoJour, int horizonJours) {
        if (consoJour == null || consoJour.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal stockIdeal = consoJour.multiply(BigDecimal.valueOf(horizonJours));
        BigDecimal suggestion = stockIdeal.subtract(stock);

        return suggestion.compareTo(BigDecimal.ZERO) > 0
                ? suggestion.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public List<PrevisionResult> getPrevisions(Long alimentId, LocalDate dateReference, Integer horizonJours) {
        if (dateReference == null) {
            throw new IllegalArgumentException("La date de référence est obligatoire");
        }

        int horizon = (horizonJours == null || horizonJours <= 0) ? PERIODE_ANALYSE_JOURS : horizonJours;

        
        LocalDate dateDebutAnalyse = dateReference.minusDays(PERIODE_ANALYSE_JOURS - 1);

        List<Aliment> aliments = this.alimentRepository.findAll();
        List<PrevisionResult> results = new ArrayList<>();

        for (Aliment aliment : aliments) {
            if (alimentId != null && !alimentId.equals(aliment.getId())) {
                continue;
            }

            BigDecimal stock = this.stockService.getStockAtDate(aliment.getId(), dateReference);
            BigDecimal consoJour = this.calculateConsumption(aliment.getId(), dateDebutAnalyse, dateReference);
            LocalDate dateRupture = this.calculateRuptureDate(dateReference, stock, consoJour);
            BigDecimal suggestion = this.suggestPurchase(stock, consoJour, horizon);

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
            result.setAlerte(joursRestants != null && joursRestants <= horizon
                    ? "Stock insuffisant"
                    : null);

            results.add(result);
        }

        return results;
    }

    public List<PrevisionResult> getAllPrevisions(LocalDate dateReference) {
        return this.getPrevisions(null, dateReference, PERIODE_ANALYSE_JOURS);
    }

    public List<Aliment> getAlimentsForFilter() {
        return this.alimentRepository.findAll();
    }

    public BigDecimal getStockTotal(LocalDate dateReference) {
        if (dateReference == null) {
            throw new IllegalArgumentException("dateReference est obligatoire");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Aliment aliment : this.alimentRepository.findAll()) {
            total = total.add(this.stockService.getStockAtDate(aliment.getId(), dateReference));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}