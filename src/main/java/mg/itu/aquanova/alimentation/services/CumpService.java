package mg.itu.aquanova.alimentation.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.repositories.LigneAchatRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class CumpService {

    private static final int ECHELLE_CALCUL = 6;
    private static final int ECHELLE_COUT = 4;

    private final LigneAchatRepository ligneAchatRepository;
    private final DistributionRepository distributionRepository;
    private final AlimentRepository alimentRepository;

    public CumpService(LigneAchatRepository ligneAchatRepository,
                       DistributionRepository distributionRepository,
                       AlimentRepository alimentRepository) {
        this.ligneAchatRepository = ligneAchatRepository;
        this.distributionRepository = distributionRepository;
        this.alimentRepository = alimentRepository;
    }

    public BigDecimal calculerCump(Long alimentId, LocalDate date) {
        if (alimentId == null || date == null) {
            return BigDecimal.ZERO;
        }

        List<Mouvement> mouvements = chargerMouvements(alimentId, date);
        if (mouvements.isEmpty()) {
            return prixCatalogue(alimentId);
        }

        BigDecimal quantiteStock = BigDecimal.ZERO;
        BigDecimal valeurStock = BigDecimal.ZERO;
        BigDecimal cump = BigDecimal.ZERO;
        boolean auMoinsUneEntree = false;

        for (Mouvement mouvement : mouvements) {
            if (mouvement.entree) {
                valeurStock = valeurStock.add(mouvement.quantite.multiply(mouvement.coutUnitaire));
                quantiteStock = quantiteStock.add(mouvement.quantite);
                if (quantiteStock.signum() > 0) {
                    cump = valeurStock.divide(quantiteStock, ECHELLE_CALCUL, RoundingMode.HALF_UP);
                }
                auMoinsUneEntree = true;
            } else {
                BigDecimal coutSortie = mouvement.coutUnitaire != null ? mouvement.coutUnitaire : cump;
                valeurStock = valeurStock.subtract(mouvement.quantite.multiply(coutSortie));
                quantiteStock = quantiteStock.subtract(mouvement.quantite);

                if (quantiteStock.signum() <= 0) {
                    quantiteStock = BigDecimal.ZERO;
                    valeurStock = BigDecimal.ZERO;
                }
            }
        }

        if (!auMoinsUneEntree) {
            return prixCatalogue(alimentId);
        }
        return cump.setScale(ECHELLE_COUT, RoundingMode.HALF_UP);
    }

    private List<Mouvement> chargerMouvements(Long alimentId, LocalDate date) {
        List<Mouvement> mouvements = new ArrayList<>();

        for (Object[] ligne : ligneAchatRepository.findEntreesStockJusqua(alimentId, date)) {
            LocalDate dateMouvement = (LocalDate) ligne[0];
            BigDecimal quantite = (BigDecimal) ligne[1];
            BigDecimal prix = (BigDecimal) ligne[2];
            if (quantite == null || prix == null || quantite.signum() <= 0) {
                continue;
            }
            mouvements.add(new Mouvement(dateMouvement, quantite, prix, true));
        }

        for (Object[] ligne : distributionRepository.findSortiesStockJusqua(alimentId, date)) {
            LocalDate dateMouvement = (LocalDate) ligne[0];
            BigDecimal quantite = (BigDecimal) ligne[1];
            BigDecimal coutFige = (BigDecimal) ligne[2];
            if (quantite == null || quantite.signum() <= 0) {
                continue;
            }
            mouvements.add(new Mouvement(dateMouvement, quantite, coutFige, false));
        }

        mouvements.sort(Comparator
                .comparing((Mouvement m) -> m.date)
                .thenComparing(m -> m.entree ? 0 : 1));

        return mouvements;
    }

    private BigDecimal prixCatalogue(Long alimentId) {
        return alimentRepository.findById(alimentId)
                .map(Aliment::getPrixUnitaire)
                .filter(prix -> prix != null)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO)
                .setScale(ECHELLE_COUT, RoundingMode.HALF_UP);
    }

    private static final class Mouvement {
        private final LocalDate date;
        private final BigDecimal quantite;
        private final BigDecimal coutUnitaire;
        private final boolean entree;

        private Mouvement(LocalDate date, BigDecimal quantite, BigDecimal coutUnitaire, boolean entree) {
            this.date = date;
            this.quantite = quantite;
            this.coutUnitaire = coutUnitaire;
            this.entree = entree;
        }
    }
}
