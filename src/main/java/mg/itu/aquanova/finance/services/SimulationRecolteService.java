package mg.itu.aquanova.finance.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.admin.service.ParametreSystemeService;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.finance.dto.RentabiliteLotDTO;
import mg.itu.aquanova.finance.dto.SimulationRecolteDTO;
import mg.itu.aquanova.finance.dto.SimulationRecolteDTO.PointSimulation;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.MortaliteRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.PrevisionRecolteService;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;

/**
 * Répond à la question que le suivi ne pose jamais : « faut-il récolter ce lot maintenant,
 * ou attendre ? »
 *
 * Attendre fait grossir les poissons — donc plus de kilos à vendre. Mais attendre coûte de
 * l'aliment chaque jour, et la mortalité continue de grignoter l'effectif. Il existe donc une
 * date qui maximise la marge, et elle n'est évidente pour personne.
 *
 * Le service balaie chaque jour d'un horizon donné et calcule, pour chacun :
 *
 *   poids(j)      = poids actuel + croissance journalière × j, plafonné au poids cible
 *   effectif(j)   = effectif actuel × (1 − taux de mortalité journalier) ^ j
 *   biomasse(j)   = effectif(j) × poids(j)
 *   ration(j)     = ICA × gain de poids du jour, ou ration d'entretien si la croissance est finie
 *   marge(j)      = biomasse(j) × prix de vente
 *                 − coûts déjà engagés (alevins + aliment distribué)
 *                 − Σ ration × coût de l'aliment sur les j jours
 *
 * La courbe a un maximum réel : la croissance plafonne au poids cible, alors que l'aliment et
 * la mortalité, eux, continuent. Passé ce point, attendre fait perdre de l'argent.
 *
 * LIMITE ASSUMÉE : la croissance est modélisée linéairement jusqu'au poids cible, alors qu'une
 * courbe de croissance réelle est sigmoïde. C'est une simplification, cohérente avec le reste
 * de l'application (la prévision de récolte utilise le même modèle linéaire).
 */
@Service
public class SimulationRecolteService {

    private static final int HORIZON_PAR_DEFAUT = 90;

    /** Une fois le poids cible atteint, les poissons ne grossissent plus mais mangent encore. */
    private static final double RATION_ENTRETIEN_PCT_BIOMASSE = 0.01;   // 1 % de la biomasse par jour

    private final LotService lotService;
    private final PrevisionRecolteService previsionRecolteService;
    private final RentabiliteLotService rentabiliteLotService;
    private final ParametreSystemeService parametreSystemeService;
    private final MortaliteRepository mortaliteRepository;
    private final DistributionRepository distributionRepository;
    private final AlimentRepository alimentRepository;
    private final VenteRepository venteRepository;

    public SimulationRecolteService(LotService lotService,
                                    PrevisionRecolteService previsionRecolteService,
                                    RentabiliteLotService rentabiliteLotService,
                                    ParametreSystemeService parametreSystemeService,
                                    MortaliteRepository mortaliteRepository,
                                    DistributionRepository distributionRepository,
                                    AlimentRepository alimentRepository,
                                    VenteRepository venteRepository) {
        this.lotService = lotService;
        this.previsionRecolteService = previsionRecolteService;
        this.rentabiliteLotService = rentabiliteLotService;
        this.parametreSystemeService = parametreSystemeService;
        this.mortaliteRepository = mortaliteRepository;
        this.distributionRepository = distributionRepository;
        this.alimentRepository = alimentRepository;
        this.venteRepository = venteRepository;
    }

    /**
     * @param prixVenteKg   hypothèse de prix ; null → prix moyen constaté pour l'espèce
     * @param coutAlimentKg hypothèse de coût ; null → coût moyen réel de l'aliment
     * @param tauxMortalite hypothèse en % / jour ; null → taux observé sur le lot
     */
    public SimulationRecolteDTO simuler(Long lotId, Double prixVenteKg, Double coutAlimentKg,
                                        Double tauxMortalite, Integer horizonJours) {

        LotModels lot = lotService.trouverParId(lotId);

        SimulationRecolteDTO simulation = new SimulationRecolteDTO();
        simulation.setLotId(lot.getId());
        simulation.setCodeLot(lot.getCode());
        simulation.setEspece(lot.getEspece() != null ? lot.getEspece().getNom() : null);
        simulation.setBassin(lot.getBassin() != null ? lot.getBassin().getReference() : null);
        simulation.setHorizonJours(horizonJours != null && horizonJours > 0 ? horizonJours : HORIZON_PAR_DEFAUT);
        simulation.setEffectifActuel(lot.getEffectifActuel());
        simulation.setPoidsMoyenActuel(lot.getPoidsMoyenActuel());
        simulation.setPoidsCible(poidsCible(lot));

        String motifImpossible = verifierCalculable(lot);
        if (motifImpossible != null) {
            simulation.setImpossible(motifImpossible);
            return simulation;
        }

        double croissance = previsionRecolteService.calculerCroissanceMoyenne(lot);
        simulation.setCroissanceJournaliere(croissance);

        RentabiliteLotDTO rentabilite = rentabiliteLotService.construirePourLot(lot);
        double coutsEngages = rentabilite.getCoutsDirects().doubleValue();
        simulation.setCoutsDejaEngages(coutsEngages);

        double prix = prixVenteKg != null ? prixVenteKg : prixMoyenEspece(lot);
        double coutAliment = coutAlimentKg != null ? coutAlimentKg : coutMoyenAliment(lot);
        double mortalite = tauxMortalite != null ? tauxMortalite : tauxMortaliteObserve(lot);

        simulation.setPrixVenteKg(prix);
        simulation.setCoutAlimentKg(coutAliment);
        simulation.setTauxMortaliteJournalier(mortalite);

        construireCourbe(simulation, lot, croissance, coutsEngages, prix, coutAliment, mortalite / 100.0);
        return simulation;
    }

    private void construireCourbe(SimulationRecolteDTO simulation, LotModels lot, double croissance,
                                  double coutsEngages, double prix, double coutAliment, double mortaliteJour) {

        double poidsCible = simulation.getPoidsCible();
        double poidsDepart = lot.getPoidsMoyenActuel();
        int effectifDepart = lot.getEffectifActuel();
        double ica = parametreSystemeService.getDouble(ParametreSystemeService.ICA_SYSTEME, 1.3);

        double coutAlimentCumule = 0;
        PointSimulation optimum = null;

        for (int jour = 0; jour <= simulation.getHorizonJours(); jour++) {

            double poids = Math.min(poidsDepart + croissance * jour, poidsCible);
            int effectif = (int) Math.round(effectifDepart * Math.pow(1 - mortaliteJour, jour));
            double biomasse = effectif * poids / 1000.0;

            if (jour > 0) {
                // Aliment consommé pendant la journée qui vient de s'écouler.
                double poidsVeille = Math.min(poidsDepart + croissance * (jour - 1), poidsCible);
                double gainJourKg = (poids - poidsVeille) * effectif / 1000.0;
                double ration = gainJourKg > 0
                        ? ica * gainJourKg
                        : RATION_ENTRETIEN_PCT_BIOMASSE * biomasse;
                coutAlimentCumule += ration * coutAliment;
            }

            double chiffreAffaires = biomasse * prix;
            double marge = chiffreAffaires - coutsEngages - coutAlimentCumule;

            PointSimulation point = new PointSimulation(
                    jour, LocalDate.now().plusDays(jour), arrondir(poids), effectif,
                    arrondir(biomasse), arrondir(chiffreAffaires), arrondir(coutAlimentCumule), arrondir(marge));

            simulation.getPoints().add(point);

            if (jour == 0) {
                simulation.setAujourdHui(point);
            }
            if (optimum == null || point.getMarge() > optimum.getMarge()) {
                optimum = point;
            }
            if (simulation.getDateCiblePoids() == null && poids >= poidsCible) {
                simulation.setDateCiblePoids(point.getDate());
            }
        }

        simulation.setOptimum(optimum);
    }

    private String verifierCalculable(LotModels lot) {
        if (lot.getEffectifActuel() == null || lot.getEffectifActuel() <= 0) {
            return "Ce lot ne contient plus de poissons : il n'y a rien à récolter.";
        }
        if (lot.getPoidsMoyenActuel() == null || lot.getPoidsMoyenActuel() <= 0) {
            return "Le poids moyen du lot n'est pas connu. Enregistrez une pesée.";
        }
        if (poidsCible(lot) == null || poidsCible(lot) <= 0) {
            return "Le poids cible de l'espèce n'est pas renseigné : impossible de projeter la croissance.";
        }
        Double croissance = previsionRecolteService.calculerCroissanceMoyenne(lot);
        if (croissance == null) {
            return "Deux pesées au minimum sont nécessaires pour estimer la croissance de ce lot.";
        }
        if (croissance <= 0) {
            return "La croissance observée sur ce lot est nulle ou négative : la projection n'aurait aucun sens.";
        }
        return null;
    }

    private Double poidsCible(LotModels lot) {
        if (lot.getEspece() == null || lot.getEspece().getPoidsCibleMoyen() == null) {
            return null;
        }
        return lot.getEspece().getPoidsCibleMoyen().doubleValue();
    }

    /** Prix moyen réellement constaté pour l'espèce, à défaut toutes espèces confondues. */
    private double prixMoyenEspece(LotModels lot) {
        Integer especeId = lot.getEspece() != null ? lot.getEspece().getId() : null;
        Double prix = especeId != null ? venteRepository.estimerPrixMoyenVenteKgParEspece(especeId) : null;
        if (prix == null || prix <= 0) {
            prix = venteRepository.estimerPrixMoyenVenteKg();
        }
        return prix != null && prix > 0 ? prix : 0.0;
    }

    /** Même chaîne de repli que la prévision financière : coût du lot, puis exploitation, puis catalogue. */
    private double coutMoyenAliment(LotModels lot) {
        Double prix = distributionRepository.findPrixMoyenAlimentByLotId(lot.getId());
        if (prix == null || prix <= 0) {
            prix = distributionRepository.findPrixMoyenAlimentGlobal();
        }
        if (prix == null || prix <= 0) {
            prix = alimentRepository.findPrixMoyenCatalogue();
        }
        return prix != null && prix > 0 ? prix : 0.0;
    }

    /**
     * Taux de mortalité journalier observé sur ce lot depuis sa mise en charge.
     * C'est une moyenne : elle lisse les épisodes ponctuels, ce qui est exactement ce qu'on veut
     * pour projeter — mais l'utilisateur peut la corriger à la main.
     */
    private double tauxMortaliteObserve(LotModels lot) {
        if (lot.getDateMiseEnCharge() == null || lot.getEffectifInitial() == null
                || lot.getEffectifInitial() <= 0) {
            return 0.0;
        }
        long jours = ChronoUnit.DAYS.between(lot.getDateMiseEnCharge(), LocalDate.now());
        if (jours <= 0) {
            return 0.0;
        }
        Integer morts = mortaliteRepository.sumNbMortsByLotId(lot.getId());
        if (morts == null || morts <= 0) {
            return 0.0;
        }
        double tauxJournalier = (double) morts / lot.getEffectifInitial() / jours * 100.0;
        return Math.round(tauxJournalier * 1000.0) / 1000.0;
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
