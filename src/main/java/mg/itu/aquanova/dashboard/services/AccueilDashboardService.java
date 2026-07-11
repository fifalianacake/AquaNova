package mg.itu.aquanova.dashboard.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.services.AlerteService;
import mg.itu.aquanova.dashboard.dto.AccueilDashboardDTO;
import mg.itu.aquanova.dashboard.dto.AccueilDashboardDTO.PointSerie;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.TransfertModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.MortaliteRepository;
import mg.itu.aquanova.production.repositories.PeseRepository;
import mg.itu.aquanova.production.repositories.RecoltesRepository;
import mg.itu.aquanova.production.repositories.TransfertRepository;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;

@Service
public class AccueilDashboardService {

    private static final int NB_MOIS_HISTORIQUE = 12;
    private static final int JOURS_FENETRE_MORTALITE = 30;
    private static final int NB_ALERTES_AFFICHEES = 5;
    private static final int ECHELLE = 2;
    private static final DateTimeFormatter FORMAT_MOIS_COURT =
            DateTimeFormatter.ofPattern("MMM", Locale.FRENCH);

    private final LotRepository lotRepository;
    private final MortaliteRepository mortaliteRepository;
    private final RecoltesRepository recoltesRepository;
    private final TransfertRepository transfertRepository;
    private final PeseRepository peseRepository;
    private final BassinsRepository bassinsRepository;
    private final VenteRepository venteRepository;
    private final AlerteService alerteService;

    public AccueilDashboardService(
            LotRepository lotRepository,
            MortaliteRepository mortaliteRepository,
            RecoltesRepository recoltesRepository,
            TransfertRepository transfertRepository,
            PeseRepository peseRepository,
            BassinsRepository bassinsRepository,
            VenteRepository venteRepository,
            AlerteService alerteService) {
        this.lotRepository = lotRepository;
        this.mortaliteRepository = mortaliteRepository;
        this.recoltesRepository = recoltesRepository;
        this.transfertRepository = transfertRepository;
        this.peseRepository = peseRepository;
        this.bassinsRepository = bassinsRepository;
        this.venteRepository = venteRepository;
        this.alerteService = alerteService;
    }

    public AccueilDashboardDTO construire() {
        AccueilDashboardDTO dto = new AccueilDashboardDTO();

        List<LotModels> lots = lotRepository.findAll();
        List<MortaliteModels> mortalites = mortaliteRepository.findAll();
        List<Recoltes> recoltes = recoltesRepository.findAll();
        List<TransfertModels> transferts = transfertRepository.findAll();
        List<Pese> pesees = peseRepository.findAll();

        appliquerBiomasseEtEspeces(dto, lots);
        appliquerBassins(dto);
        appliquerMortalite(dto, lots, mortalites);
        appliquerVentes(dto);
        appliquerEvolutionBiomasse(dto, lots, mortalites, recoltes, transferts, pesees);
        appliquerAlertes(dto);

        return dto;
    }

    private void appliquerBiomasseEtEspeces(AccueilDashboardDTO dto, List<LotModels> lots) {
        BigDecimal biomasseTotale = BigDecimal.ZERO;
        Map<String, BigDecimal> parEspece = new LinkedHashMap<>();
        long lotsActifs = 0;

        for (LotModels lot : lots) {
            if (!estActif(lot)) {
                continue;
            }
            lotsActifs++;

            BigDecimal biomasse = biomasseKg(lot.getEffectifActuel(), lot.getPoidsMoyenActuel());
            biomasseTotale = biomasseTotale.add(biomasse);

            String espece = lot.getEspece() != null && lot.getEspece().getNom() != null
                    ? lot.getEspece().getNom()
                    : "Espèce inconnue";
            parEspece.merge(espece, biomasse, BigDecimal::add);
        }

        dto.setLotsActifs(lotsActifs);
        dto.setBiomasseTotaleKg(arrondir(biomasseTotale));

        List<PointSerie> repartition = new ArrayList<>();
        parEspece.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> repartition.add(new PointSerie(e.getKey(), arrondir(e.getValue()))));
        dto.setRepartitionEspeces(repartition);
    }

    private BigDecimal biomasseKg(Integer effectif, Double poidsMoyenGrammes) {
        if (effectif == null || effectif <= 0 || poidsMoyenGrammes == null || poidsMoyenGrammes <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(effectif)
                .multiply(BigDecimal.valueOf(poidsMoyenGrammes))
                .divide(BigDecimal.valueOf(1000), ECHELLE, RoundingMode.HALF_UP);
    }

    private void appliquerBassins(AccueilDashboardDTO dto) {
        List<Bassin> bassins = bassinsRepository.findAll();
        long occupes = bassins.stream()
                .filter(b -> b.getStatut() != null && b.getStatut().getLibelle() == LibelleStatutBassin.OCCUPE)
                .count();
        long libres = bassins.stream()
                .filter(b -> b.getStatut() != null && b.getStatut().getLibelle() == LibelleStatutBassin.LIBRE)
                .count();

        dto.setBassinsTotal(bassins.size());
        dto.setBassinsOccupes(occupes);
        dto.setBassinsLibres(libres);
    }

    private void appliquerMortalite(AccueilDashboardDTO dto, List<LotModels> lots,
                                    List<MortaliteModels> mortalites) {
        LocalDate depuis = LocalDate.now().minusDays(JOURS_FENETRE_MORTALITE);

        long morts = mortalites.stream()
                .filter(m -> m.getDateMortalite() != null && !m.getDateMortalite().isBefore(depuis))
                .mapToLong(m -> m.getNbMorts() != null ? m.getNbMorts() : 0)
                .sum();

        long effectifVivant = lots.stream()
                .filter(this::estActif)
                .mapToLong(l -> l.getEffectifActuel() != null ? l.getEffectifActuel() : 0)
                .sum();

        dto.setMortsSur30j(morts);

        long population = effectifVivant + morts;
        if (population > 0) {
            dto.setTauxMortalite30j(BigDecimal.valueOf(morts)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(population), ECHELLE, RoundingMode.HALF_UP));
        }
    }

    private void appliquerVentes(AccueilDashboardDTO dto) {
        YearMonth moisCourant = YearMonth.now();
        BigDecimal ventesMois = chiffreAffairesDuMois(moisCourant);
        BigDecimal ventesMoisPrecedent = chiffreAffairesDuMois(moisCourant.minusMonths(1));

        dto.setVentesDuMois(arrondir(ventesMois));
        dto.setVariationVentesPct(variationPct(ventesMoisPrecedent, ventesMois));

        List<PointSerie> serie = new ArrayList<>();
        for (YearMonth mois : douzeDerniersMois()) {
            serie.add(new PointSerie(libelleMois(mois), arrondir(chiffreAffairesDuMois(mois))));
        }
        dto.setVentesMensuelles(serie);
    }

    private BigDecimal chiffreAffairesDuMois(YearMonth mois) {
        Double ca = venteRepository.sumChiffreAffaires(mois.atDay(1), mois.atEndOfMonth());
        return ca != null ? BigDecimal.valueOf(ca) : BigDecimal.ZERO;
    }

    private void appliquerEvolutionBiomasse(AccueilDashboardDTO dto, List<LotModels> lots,
                                            List<MortaliteModels> mortalites, List<Recoltes> recoltes,
                                            List<TransfertModels> transferts, List<Pese> pesees) {
        List<PointSerie> serie = new ArrayList<>();
        BigDecimal premiere = null;
        BigDecimal derniere = BigDecimal.ZERO;

        for (YearMonth mois : douzeDerniersMois()) {
            LocalDate finMois = mois.atEndOfMonth();
            BigDecimal biomasse = BigDecimal.ZERO;

            for (LotModels lot : lots) {
                if (lot.getDateMiseEnCharge() == null || lot.getDateMiseEnCharge().isAfter(finMois)) {
                    continue; // le lot n'existait pas encore
                }
                int effectif = effectifALaDate(lot, finMois, mortalites, recoltes, transferts);
                if (effectif <= 0) {
                    continue;
                }
                Double poidsMoyen = poidsMoyenALaDate(lot, finMois, pesees);
                biomasse = biomasse.add(biomasseKg(effectif, poidsMoyen));
            }

            biomasse = arrondir(biomasse);
            if (premiere == null) {
                premiere = biomasse;
            }
            derniere = biomasse;
            serie.add(new PointSerie(libelleMois(mois), biomasse));
        }

        dto.setEvolutionBiomasse(serie);
        dto.setVariationBiomassePct(variationPct(premiere, derniere));
    }

    private int effectifALaDate(LotModels lot, LocalDate date, List<MortaliteModels> mortalites,
                                List<Recoltes> recoltes, List<TransfertModels> transferts) {
        int effectif = lot.getEffectifInitial() != null ? lot.getEffectifInitial() : 0;

        for (MortaliteModels m : mortalites) {
            if (memeLot(m.getLot(), lot) && nonPosterieur(m.getDateMortalite(), date)) {
                effectif -= m.getNbMorts() != null ? m.getNbMorts() : 0;
            }
        }
        for (Recoltes r : recoltes) {
            if (memeLot(r.getLot(), lot) && nonPosterieur(r.getDateRecolte(), date)) {
                effectif -= r.getEffectifRecolte();
            }
        }
        for (TransfertModels t : transferts) {
            if (memeLot(t.getLotSource(), lot) && nonPosterieur(t.getDateTransfert(), date)) {
                effectif -= t.getEffectif() != null ? t.getEffectif() : 0;
            }
        }

        return Math.max(effectif, 0);
    }

    private Double poidsMoyenALaDate(LotModels lot, LocalDate date, List<Pese> pesees) {
        return pesees.stream()
                .filter(p -> memeLot(p.getLot(), lot) && nonPosterieur(p.getDatePesee(), date))
                .filter(p -> p.getPoidsMoyen() != null)
                .max(Comparator.comparing(Pese::getDatePesee))
                .map(p -> p.getPoidsMoyen().doubleValue())
                .orElse(lot.getPoidsMoyenInitial());
    }

    private void appliquerAlertes(AccueilDashboardDTO dto) {
        List<Alerte> actives = alerteService.getAlertesActives();

        dto.setNbAlertesActives(actives.size());
        dto.setNbAlertesCritiques(actives.stream()
                .filter(a -> a.getNiveauCriticite() == NiveauCriticite.CRITIQUE)
                .count());

        dto.setAlertesRecentes(actives.stream()
                .sorted(Comparator
                        .comparing((Alerte a) -> a.getNiveauCriticite() == NiveauCriticite.CRITIQUE ? 0 : 1)
                        .thenComparing(Alerte::getDateCreation, Comparator.reverseOrder()))
                .limit(NB_ALERTES_AFFICHEES)
                .toList());
    }

    private boolean estActif(LotModels lot) {
        return lot.getStatutLot() != null
                && lot.getStatutLot().getLibelle() != StatutLotEnum.CLOTURE
                && lot.getStatutLot().getLibelle() != StatutLotEnum.ANNULE;
    }

    private boolean memeLot(LotModels candidat, LotModels lot) {
        return candidat != null && candidat.getId() != null && candidat.getId().equals(lot.getId());
    }

    private boolean nonPosterieur(LocalDate date, LocalDate borne) {
        return date != null && !date.isAfter(borne);
    }

    private List<YearMonth> douzeDerniersMois() {
        List<YearMonth> mois = new ArrayList<>();
        YearMonth courant = YearMonth.now().minusMonths(NB_MOIS_HISTORIQUE - 1L);
        for (int i = 0; i < NB_MOIS_HISTORIQUE; i++) {
            mois.add(courant.plusMonths(i));
        }
        return mois;
    }

    private String libelleMois(YearMonth mois) {
        return mois.format(FORMAT_MOIS_COURT);
    }

    private BigDecimal variationPct(BigDecimal reference, BigDecimal valeur) {
        if (reference == null || reference.signum() <= 0) {
            return null;
        }
        return valeur.subtract(reference)
                .multiply(BigDecimal.valueOf(100))
                .divide(reference, ECHELLE, RoundingMode.HALF_UP);
    }

    private BigDecimal arrondir(BigDecimal valeur) {
        return valeur.setScale(ECHELLE, RoundingMode.HALF_UP);
    }
}
