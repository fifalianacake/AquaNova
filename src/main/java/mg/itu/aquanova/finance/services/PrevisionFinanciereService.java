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
import mg.itu.aquanova.vente.repositories.VenteRepository;

@Service
public class PrevisionFinanciereService {
    private final LotRepository lotRepository;
    private final VenteRepository venteRepository;
    private final DistributionRepository distributionRepository;
    private final DistributionService distributionService;
    private final PrevisionRecolteService previsionRecolteService;

    public PrevisionFinanciereService(LotRepository lotRepository, VenteRepository venteRepository,
            DistributionRepository distributionRepository, DistributionService distributionService,
            PrevisionRecolteService previsionRecolteService) {
        this.lotRepository = lotRepository;
        this.venteRepository = venteRepository;
        this.distributionRepository = distributionRepository;
        this.distributionService = distributionService;
        this.previsionRecolteService = previsionRecolteService;
    }

    private Double estimerPrixMoyenVenteKg() {
        return venteRepository.estimerPrixMoyenVenteKg();
    }

    private Double estimerBiomasseVendable(Long lotId) {
        Double total = 0.0;

        if (lotId == null) {
            return total;
        }

        LotModels lot = lotRepository.findById(lotId).orElse(null);

        if (lot.getPoidsMoyenActuel() == null || lot.getEffectifActuel() == null) {
            return total;
        }

        total = (lot.getPoidsMoyenActuel() * lot.getEffectifActuel()) / 1000.0;

        return total;
    }

    private Double estimerCoutsFuturs(Long lotId) {
        Double total = 0.0;

        if (lotId == null) {
            return total;
        }

        LocalDate dateRecolteEstimee = previsionRecolteService.estimerDateRecolte(lotId);
        if (dateRecolteEstimee == null) {
            return total;
        }

        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), dateRecolteEstimee);
        if (joursRestants <= 0) {
            return total;
        }

        // Ration journalière théorique (kg/jour, pour tout le lot) basée sur l'ICA système
        // et le gain de poids cible restant à atteindre avant la récolte (même formule que
        // DistributionService, réutilisée ici pour projeter le coût sur les jours restants).
        BigDecimal rationJournaliereKg = distributionService.calculRationTheoriqueCible(lotId);
        Double prixMoyenAlimentKg = distributionRepository.findPrixMoyenAlimentByLotId(lotId);

        if (rationJournaliereKg == null || prixMoyenAlimentKg == null) {
            return total;
        }

        total = rationJournaliereKg.doubleValue() * prixMoyenAlimentKg * joursRestants;

        return total;
    }

    public List<PrevisionFinanciereDTO> genererPrevisions(LocalDate dateDebut, LocalDate dateFin) {
        List<PrevisionFinanciereDTO> previsions = new ArrayList<>();

        List<LotModels> listeLots = lotRepository.findAll();

        Double prixMoyenVenteKg = estimerPrixMoyenVenteKg();

        for (LotModels lot : listeLots) {

            if (lot.getId() == null) {
                continue;
            }

            LocalDate dateRecolteEstimee = previsionRecolteService.estimerDateRecolte(lot.getId());

            if (dateRecolteEstimee == null || dateRecolteEstimee.isBefore(dateDebut)
                    || dateRecolteEstimee.isAfter(dateFin)) {
                continue;
            }

            Double biomassePrevue = estimerBiomasseVendable(lot.getId());
            String espece = lot.getEspece().getNom() != null ? lot.getEspece().getNom() : "Inconnue";
            String codeLot = lot.getCode() != null ? lot.getCode() : "Inconnu";
            Double coutPrevisionnel = estimerCoutsFuturs(lot.getId());
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
