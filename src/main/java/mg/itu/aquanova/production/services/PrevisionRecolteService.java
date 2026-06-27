package mg.itu.aquanova.production.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.production.dto.PrevisionRecolteResult;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.PeseRepository;

@Service
public class PrevisionRecolteService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal SEUIL_PROCHE_RECOLTE = new BigDecimal("0.90");

    private final LotRepository lotRepository;
    private final PeseRepository peseRepository;

    public PrevisionRecolteService(LotRepository lotRepository, PeseRepository peseRepository) {
        this.lotRepository = lotRepository;
        this.peseRepository = peseRepository;
    }

    public BigDecimal calculerCroissanceMoyenne(Long lotId) {
        LotModels lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot introuvable: " + lotId));
        return calculerCroissanceMoyenne(lot);
    }

    public BigDecimal calculerCroissanceMoyenne(LotModels lot) {
        List<Pese> pesees = peseRepository.findByLotIdOrderByDatePeseeAsc(lot.getId());
        if (pesees.size() < 2) {
            return null;
        }

        Pese premierePesee = pesees.get(0);
        Pese dernierePesee = pesees.get(pesees.size() - 1);
        long nombreJours = ChronoUnit.DAYS.between(premierePesee.getDatePesee(), dernierePesee.getDatePesee());
        if (nombreJours <= 0 || premierePesee.getPoidsMoyen() == null || dernierePesee.getPoidsMoyen() == null) {
            return null;
        }

        return dernierePesee.getPoidsMoyen()
                .subtract(premierePesee.getPoidsMoyen())
                .divide(BigDecimal.valueOf(nombreJours), 3, RoundingMode.HALF_UP);
    }

    public LocalDate estimerDateRecolte(Long lotId) {
        LotModels lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot introuvable: " + lotId));
        return construirePrevision(lot).getDateRecolteEstimee();
    }

    public List<PrevisionRecolteResult> rechercher(PrevisionRecolteFilter filter) {
        return lotRepository.findAll().stream()
                .filter(this::estLotActif)
                .filter(lot -> correspondAuxFiltres(lot, filter))
                .map(this::construirePrevision)
                .filter(prevision -> correspondALaPeriode(prevision, filter))
                .toList();
    }

    public List<PrevisionRecolteResult> getLotsProchesRecolte() {
        return getLotsProchesRecolte(null);
    }

    public List<PrevisionRecolteResult> getLotsProchesRecolte(PrevisionRecolteFilter filter) {
        return rechercher(filter).stream()
                .filter(PrevisionRecolteResult::isProcheRecolte)
                .toList();
    }

    private PrevisionRecolteResult construirePrevision(LotModels lot) {
        PrevisionRecolteResult result = new PrevisionRecolteResult();
        result.setLot(lot);
        result.setPoidsMoyenActuel(resolvePoidsMoyenActuel(lot));
        result.setPoidsCible(resolvePoidsCible(lot));

        if (result.getPoidsCible() == null || result.getPoidsCible().compareTo(ZERO) <= 0) {
            result.setAlerte("Poids cible non renseigné pour l'espèce.");
            return result;
        }
        if (result.getPoidsMoyenActuel() == null || result.getPoidsMoyenActuel().compareTo(ZERO) <= 0) {
            result.setAlerte("Poids moyen actuel non disponible.");
            return result;
        }

        result.setProcheRecolte(estProcheDuPoidsCible(result.getPoidsMoyenActuel(), result.getPoidsCible()));

        BigDecimal croissanceMoyenne = calculerCroissanceMoyenne(lot);
        result.setCroissanceMoyenneJournaliere(croissanceMoyenne);

        if (result.getPoidsMoyenActuel().compareTo(result.getPoidsCible()) >= 0) {
            result.setDateRecolteEstimee(LocalDate.now());
            result.setAlerte("Poids cible atteint ou dépassé.");
            return result;
        }
        if (croissanceMoyenne == null) {
            result.setAlerte("Pas assez de pesées pour estimer la date de récolte.");
            return result;
        }
        if (croissanceMoyenne.compareTo(ZERO) <= 0) {
            result.setAlerte("Croissance moyenne insuffisante ou négative.");
            return result;
        }

        BigDecimal poidsRestant = result.getPoidsCible().subtract(result.getPoidsMoyenActuel());
        long joursRestants = poidsRestant.divide(croissanceMoyenne, 0, RoundingMode.CEILING).longValue();
        result.setDateRecolteEstimee(LocalDate.now().plusDays(joursRestants));
        return result;
    }

    private BigDecimal resolvePoidsMoyenActuel(LotModels lot) {
        if (lot.getPoidsMoyenActuel() != null) {
            return BigDecimal.valueOf(lot.getPoidsMoyenActuel());
        }

        List<Pese> pesees = peseRepository.findByLotIdOrderByDatePeseeDesc(lot.getId());
        if (pesees.isEmpty()) {
            return null;
        }
        return pesees.get(0).getPoidsMoyen();
    }

    private BigDecimal resolvePoidsCible(LotModels lot) {
        if (lot.getEspece() == null) {
            return null;
        }
        return lot.getEspece().getPoidsCibleMoyen();
    }

    private boolean estProcheDuPoidsCible(BigDecimal poidsMoyenActuel, BigDecimal poidsCible) {
        return poidsMoyenActuel.compareTo(poidsCible.multiply(SEUIL_PROCHE_RECOLTE)) >= 0;
    }

    private boolean estLotActif(LotModels lot) {
        return lot.getStatutLot() == null || lot.getStatutLot().getLibelle() != StatutLotEnum.CLOTURE;
    }

    private boolean correspondAuxFiltres(LotModels lot, PrevisionRecolteFilter filter) {
        if (filter == null) {
            return true;
        }
        if (filter.getLotId() != null && !filter.getLotId().equals(lot.getId())) {
            return false;
        }
        if (filter.getEspeceId() != null
                && (lot.getEspece() == null || !filter.getEspeceId().equals(lot.getEspece().getId()))) {
            return false;
        }
        return filter.getBassinId() == null
                || (lot.getBassin() != null && filter.getBassinId().equals(lot.getBassin().getId()));
    }

    private boolean correspondALaPeriode(PrevisionRecolteResult prevision, PrevisionRecolteFilter filter) {
        if (filter == null || (filter.getDateDebut() == null && filter.getDateFin() == null)) {
            return true;
        }
        if (prevision.getDateRecolteEstimee() == null) {
            return false;
        }
        if (filter.getDateDebut() != null && prevision.getDateRecolteEstimee().isBefore(filter.getDateDebut())) {
            return false;
        }
        return filter.getDateFin() == null || !prevision.getDateRecolteEstimee().isAfter(filter.getDateFin());
    }
}
