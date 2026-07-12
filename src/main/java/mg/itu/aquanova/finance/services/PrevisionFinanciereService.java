package mg.itu.aquanova.finance.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.alimentation.services.DistributionService;
import mg.itu.aquanova.finance.dto.PrevisionFinanciereDTO;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.services.PrevisionRecolteService;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;

@Service
public class PrevisionFinanciereService {
    private final LotRepository lotRepository;
    private final VenteRepository venteRepository;
    private final DistributionRepository distributionRepository;
    private final DistributionService distributionService;
    private final PrevisionRecolteService previsionRecolteService;
    private final AlimentRepository alimentRepository;

    public PrevisionFinanciereService(LotRepository lotRepository, VenteRepository venteRepository,
            DistributionRepository distributionRepository, DistributionService distributionService,
            PrevisionRecolteService previsionRecolteService, AlimentRepository alimentRepository) {
        this.lotRepository = lotRepository;
        this.venteRepository = venteRepository;
        this.distributionRepository = distributionRepository;
        this.distributionService = distributionService;
        this.previsionRecolteService = previsionRecolteService;
        this.alimentRepository = alimentRepository;
    }

    private Double estimerPrixMoyenVenteKg(Integer especeId) {
        Double prixEspece = especeId != null ? venteRepository.estimerPrixMoyenVenteKgParEspece(especeId) : null;
        if (prixEspece != null && prixEspece > 0) {
            return prixEspece;
        }
        // Repli : aucune vente encore enregistrée pour cette espèce, on utilise le prix moyen toutes espèces.
        return venteRepository.estimerPrixMoyenVenteKg();
    }

    private Double estimerBiomasseVendable(LotModels lot, LocalDate dateRecolteEstimee) {
        if (lot == null || lot.getEffectifActuel() == null) {
            return 0.0;
        }

        Double poidsMoyen = previsionRecolteService.estimerPoidsMoyenA(lot, dateRecolteEstimee);
        if (poidsMoyen == null) {
            poidsMoyen = lot.getPoidsMoyenActuel();
        }
        if (poidsMoyen == null) {
            return 0.0;
        }

        return (poidsMoyen * lot.getEffectifActuel()) / 1000.0;
    }

    private Double estimerPrixAlimentKg(Long lotId) {
        Double prixDuLot = distributionRepository.findPrixMoyenAlimentByLotId(lotId);
        if (prixDuLot != null && prixDuLot > 0) {
            return prixDuLot;
        }

        Double prixExploitation = distributionRepository.findPrixMoyenAlimentGlobal();
        if (prixExploitation != null && prixExploitation > 0) {
            return prixExploitation;
        }

        Double prixCatalogue = alimentRepository.findPrixMoyenCatalogue();
        return (prixCatalogue != null && prixCatalogue > 0) ? prixCatalogue : null;
    }

    private Double estimerCoutsFuturs(Long lotId, LocalDate dateRecolteEstimee) {
        Double total = 0.0;

        if (lotId == null || dateRecolteEstimee == null) {
            return total;
        }

        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), dateRecolteEstimee);
        if (joursRestants <= 0) {
            return total;
        }

        BigDecimal rationJournaliereKg = distributionService.calculRationTheoriqueCible(lotId);
        Double prixMoyenAlimentKg = estimerPrixAlimentKg(lotId);

        if (rationJournaliereKg == null || prixMoyenAlimentKg == null) {
            return total;
        }

        total = rationJournaliereKg.doubleValue() * prixMoyenAlimentKg * joursRestants;

        return total;
    }

    public List<PrevisionFinanciereDTO> genererPrevisions(LocalDate dateDebut, LocalDate dateFin) {
        List<PrevisionFinanciereDTO> previsions = new ArrayList<>();

        List<LotModels> listeLots = lotRepository.findAll();

        for (LotModels lot : listeLots) {

            if (lot.getId() == null) {
                continue;
            }

            LocalDate dateRecolteEstimee = previsionRecolteService.estimerDateRecolte(lot.getId());

            if (dateRecolteEstimee == null || dateRecolteEstimee.isBefore(dateDebut)
                    || dateRecolteEstimee.isAfter(dateFin)) {
                continue;
            }

            Integer especeId = lot.getEspece() != null ? lot.getEspece().getId() : null;
            Double prixMoyenVenteKg = estimerPrixMoyenVenteKg(especeId);
            Double biomassePrevue = estimerBiomasseVendable(lot, dateRecolteEstimee);
            String espece = (lot.getEspece() != null && lot.getEspece().getNom() != null)
                    ? lot.getEspece().getNom() : "Inconnue";
            String codeLot = lot.getCode() != null ? lot.getCode() : "Inconnu";
            Double coutPrevisionnel = estimerCoutsFuturs(lot.getId(), dateRecolteEstimee);
            Double caPrevisionnel = biomassePrevue * prixMoyenVenteKg;
            Double profitPrevisionnel = caPrevisionnel - coutPrevisionnel;
            Double margePrevisionnelle = (caPrevisionnel != 0) ? (profitPrevisionnel / caPrevisionnel) * 100 : 0.0;

            PrevisionFinanciereDTO prevision = new PrevisionFinanciereDTO();
            prevision.setLotId(lot.getId());
            prevision.setCodeLot(codeLot);
            prevision.setEspece(espece);
            prevision.setBiomassePrevue(biomassePrevue);
            prevision.setPrixMoyenVenteKg(prixMoyenVenteKg);
            prevision.setCaPrevisionnel(caPrevisionnel);
            prevision.setCoutPrevisionnel(coutPrevisionnel);
            prevision.setProfitPrevisionnel(profitPrevisionnel);
            prevision.setMargePrevisionnelle(margePrevisionnelle);

            previsions.add(prevision);
        }

        return previsions;
    }
}
