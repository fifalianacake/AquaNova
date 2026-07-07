package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.admin.service.ParametreSystemeService;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.alimentation.models.PrevisionResult;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrevisionService {

    private final AlimentRepository alimentRepository;
    private final DistributionRepository distributionRepository;
    private final StockService stockService;
    private final ParametreSystemeService parametreSystemeService;

    public PrevisionService(AlimentRepository alimentRepository,
                            DistributionRepository distributionRepository,
                            StockService stockService,
                            ParametreSystemeService parametreSystemeService) {
        this.alimentRepository = alimentRepository;
        this.distributionRepository = distributionRepository;
        this.stockService = stockService;
        this.parametreSystemeService = parametreSystemeService;
    }

    public Double calculateConsumption(Long alimentId, LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("dateDebut et dateFin sont obligatoires");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("dateDebut doit être avant dateFin");
        }

        BigDecimal totalDistribueDecimal = this.distributionRepository
                .sumQuantiteByAlimentIdAndDateBetween(alimentId, dateDebut, dateFin);

        Double totalDistribue = 0.0;
        if (totalDistribueDecimal != null) {
            totalDistribue = totalDistribueDecimal.doubleValue();
        }

        long joursCalendaires = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        if (joursCalendaires <= 0) {
            return 0.0;
        }
        return round2(totalDistribue / joursCalendaires);
    }

    public LocalDate calculateRuptureDate(LocalDate dateReference, Double stock, Double consoJour) {
        if (dateReference == null) {
            throw new IllegalArgumentException("dateReference est obligatoire");
        }
        if (stock == null || consoJour == null || consoJour == 0.0) {
            return null;
        }

        long joursRestants = (long) Math.floor(stock / consoJour);

        return dateReference.plusDays(joursRestants);
    }

    public Double suggestPurchase(Double stock, Double consoJour, int horizonJours) {
        if (stock == null || consoJour == null || consoJour == 0.0) {
            return 0.0;
        }

        Double stockIdeal = consoJour * horizonJours;
        Double suggestion = stockIdeal - stock;

        return suggestion > 0.0 ? round2(suggestion) : 0.0;
    }

    public List<PrevisionResult> getPrevisions(Long alimentId, LocalDate dateReference, Integer horizonJours) {
        if (dateReference == null) {
            throw new IllegalArgumentException("La date de référence est obligatoire");
        }

        int periodeAnalyse = getPeriodeAnalyseJours();
        int horizon = (horizonJours == null || horizonJours <= 0) ? getHorizonPrevisionJours() : horizonJours;

        
        LocalDate dateDebutAnalyse = dateReference.minusDays(periodeAnalyse - 1L);

        List<Aliment> aliments = this.alimentRepository.findAll();
        List<PrevisionResult> results = new ArrayList<>();

        for (Aliment aliment : aliments) {
            if (alimentId != null && !alimentId.equals(aliment.getId())) {
                continue;
            }

            Double stock = this.stockService.getStockAtDate(aliment.getId(), dateReference);
            Double consoJour = this.calculateConsumption(aliment.getId(), dateDebutAnalyse, dateReference);
            LocalDate dateRupture = this.calculateRuptureDate(dateReference, stock, consoJour);
            Double suggestion = this.suggestPurchase(stock, consoJour, horizon);

            Long joursRestants = null;
            if (consoJour != null && consoJour > 0.0) {
                joursRestants = (long) Math.floor(stock / consoJour);
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
        return this.getPrevisions(null, dateReference, null);
    }

    public List<Aliment> getAlimentsForFilter() {
        return this.alimentRepository.findAll();
    }

    public Double getStockTotal(LocalDate dateReference) {
        if (dateReference == null) {
            throw new IllegalArgumentException("dateReference est obligatoire");
        }

        Double total = 0.0;

        for (Aliment aliment : this.alimentRepository.findAll()) {
            total += this.stockService.getStockAtDate(aliment.getId(), dateReference);
        }

        return round2(total);
    }

    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public int getPeriodeAnalyseJours() {
        return Math.max(
                1,
                parametreSystemeService.getInteger(ParametreSystemeService.PERIODE_ANALYSE_CONSO_JOURS, 30));
    }

    public int getHorizonPrevisionJours() {
        return Math.max(
                1,
                parametreSystemeService.getInteger(ParametreSystemeService.HORIZON_PREVISION_STOCK_JOURS, 30));
    }
}
