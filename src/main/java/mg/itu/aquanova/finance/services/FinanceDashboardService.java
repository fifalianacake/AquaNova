package mg.itu.aquanova.finance.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.repositories.DepenseRepository;
import mg.itu.aquanova.achat.repositories.LigneAchatRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.finance.dto.FinanceDashboardDTO;
import mg.itu.aquanova.finance.dto.FinanceEvolutionDTO;
import mg.itu.aquanova.finance.dto.RentabiliteLotDTO;
import mg.itu.aquanova.finance.models.StatutRentabilite;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;

@Service
public class FinanceDashboardService {

    private static final int ECHELLE_MONTANT = 2;
    private static final DateTimeFormatter FORMAT_MOIS = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int NB_LOTS_AFFICHES = 5;

    private final VenteRepository venteRepository;
    private final DepenseRepository depenseRepository;
    private final LigneAchatRepository ligneAchatRepository;
    private final DistributionRepository distributionRepository;
    private final LotRepository lotRepository;
    private final RentabiliteLotService rentabiliteLotService;

    public FinanceDashboardService(
            VenteRepository venteRepository,
            DepenseRepository depenseRepository,
            LigneAchatRepository ligneAchatRepository,
            DistributionRepository distributionRepository,
            LotRepository lotRepository,
            RentabiliteLotService rentabiliteLotService) {
        this.venteRepository = venteRepository;
        this.depenseRepository = depenseRepository;
        this.ligneAchatRepository = ligneAchatRepository;
        this.distributionRepository = distributionRepository;
        this.lotRepository = lotRepository;
        this.rentabiliteLotService = rentabiliteLotService;
    }

    public FinanceDashboardDTO getDashboard(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now().withDayOfYear(1);
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();

        FinanceDashboardDTO dto = new FinanceDashboardDTO();
        dto.setDateDebut(debut);
        dto.setDateFin(fin);

        BigDecimal chiffreAffaires = calculerChiffreAffaires(debut, fin);
        BigDecimal coutAlevins = nonNull(ligneAchatRepository.sumMontantAlevinsEntre(debut, fin));
        BigDecimal coutAlimentation = nonNull(distributionRepository.sumCoutAlimentationEntre(debut, fin));
        BigDecimal coutsDirects = coutAlevins.add(coutAlimentation);
        BigDecimal margeBrute = chiffreAffaires.subtract(coutsDirects);

        BigDecimal depenses = nonNull(depenseRepository.sumMontantEntre(debut, fin));
        BigDecimal profitNet = margeBrute.subtract(depenses);

        dto.setChiffreAffaires(arrondir(chiffreAffaires));
        dto.setCoutAlevins(arrondir(coutAlevins));
        dto.setCoutAlimentation(arrondir(coutAlimentation));
        dto.setCoutsDirects(arrondir(coutsDirects));
        dto.setMargeBrute(arrondir(margeBrute));
        dto.setDepenses(arrondir(depenses));
        dto.setProfitNet(arrondir(profitNet));

        if (chiffreAffaires.signum() > 0) {
            dto.setTauxMargeBrute(pourcentage(margeBrute, chiffreAffaires));
            dto.setTauxMargeNette(pourcentage(profitNet, chiffreAffaires));
        }

        Long nbVentes = venteRepository.countVentes(debut, fin);
        dto.setNombreVentes(nbVentes != null ? nbVentes : 0L);

        dto.setDepensesParCategorie(chargerDepensesParCategorie(debut, fin));
        dto.setEvolution(construireEvolution(debut, fin));

        appliquerRentabiliteDesLots(dto);

        return dto;
    }

    private BigDecimal calculerChiffreAffaires(LocalDate debut, LocalDate fin) {
        Double ca = venteRepository.sumChiffreAffaires(debut, fin);
        return ca != null ? BigDecimal.valueOf(ca) : BigDecimal.ZERO;
    }

    private List<FinanceDashboardDTO.LigneMontant> chargerDepensesParCategorie(LocalDate debut, LocalDate fin) {
        List<FinanceDashboardDTO.LigneMontant> lignes = new ArrayList<>();
        for (Object[] ligne : depenseRepository.sumMontantParCategorieEntre(debut, fin)) {
            String libelle = ligne[0] != null ? ligne[0].toString() : "Sans catégorie";
            BigDecimal montant = ligne[1] != null ? new BigDecimal(ligne[1].toString()) : BigDecimal.ZERO;
            lignes.add(new FinanceDashboardDTO.LigneMontant(libelle, arrondir(montant)));
        }
        return lignes;
    }

    private List<FinanceEvolutionDTO> construireEvolution(LocalDate debut, LocalDate fin) {
        List<FinanceEvolutionDTO> evolution = new ArrayList<>();

        YearMonth moisCourant = YearMonth.from(debut);
        YearMonth dernierMois = YearMonth.from(fin);

        while (!moisCourant.isAfter(dernierMois)) {
            LocalDate debutMois = maxDate(moisCourant.atDay(1), debut);
            LocalDate finMois = minDate(moisCourant.atEndOfMonth(), fin);

            BigDecimal ca = calculerChiffreAffaires(debutMois, finMois);
            BigDecimal couts = nonNull(ligneAchatRepository.sumMontantAlevinsEntre(debutMois, finMois))
                    .add(nonNull(distributionRepository.sumCoutAlimentationEntre(debutMois, finMois)));
            BigDecimal depensesMois = nonNull(depenseRepository.sumMontantEntre(debutMois, finMois));
            BigDecimal margeMois = ca.subtract(couts);

            FinanceEvolutionDTO point = new FinanceEvolutionDTO(moisCourant.format(FORMAT_MOIS));
            point.setChiffreAffaires(arrondir(ca));
            point.setCoutsDirects(arrondir(couts));
            point.setMargeBrute(arrondir(margeMois));
            point.setDepenses(arrondir(depensesMois));
            point.setProfitNet(arrondir(margeMois.subtract(depensesMois)));
            evolution.add(point);

            moisCourant = moisCourant.plusMonths(1);
        }

        return evolution;
    }

    private void appliquerRentabiliteDesLots(FinanceDashboardDTO dto) {
        List<RentabiliteLotDTO> rentabilites = lotRepository.findAll().stream()
                .map((LotModels lot) -> rentabiliteLotService.construirePourLot(lot))
                .toList();

        dto.setLotsRentables(compter(rentabilites, StatutRentabilite.RENTABLE));
        dto.setLotsDeficitaires(compter(rentabilites, StatutRentabilite.DEFICITAIRE));
        dto.setLotsNonCalculables(compter(rentabilites, StatutRentabilite.NON_CALCULABLE));

        dto.setTopLotsRentables(rentabilites.stream()
                .filter(r -> r.getStatutRentabilite() == StatutRentabilite.RENTABLE)
                .sorted(Comparator.comparing(RentabiliteLotDTO::getMargeBrute).reversed())
                .limit(NB_LOTS_AFFICHES)
                .toList());

        dto.setLotsDeficitairesDetail(rentabilites.stream()
                .filter(r -> r.getStatutRentabilite() == StatutRentabilite.DEFICITAIRE)
                .sorted(Comparator.comparing(RentabiliteLotDTO::getMargeBrute))
                .limit(NB_LOTS_AFFICHES)
                .toList());
    }

    private long compter(List<RentabiliteLotDTO> rentabilites, StatutRentabilite statut) {
        return rentabilites.stream().filter(r -> r.getStatutRentabilite() == statut).count();
    }

    private BigDecimal pourcentage(BigDecimal numerateur, BigDecimal denominateur) {
        return numerateur.multiply(BigDecimal.valueOf(100))
                .divide(denominateur, ECHELLE_MONTANT, RoundingMode.HALF_UP);
    }

    private BigDecimal nonNull(BigDecimal valeur) {
        return valeur != null ? valeur : BigDecimal.ZERO;
    }

    private BigDecimal arrondir(BigDecimal valeur) {
        return valeur.setScale(ECHELLE_MONTANT, RoundingMode.HALF_UP);
    }

    private LocalDate maxDate(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalDate minDate(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
