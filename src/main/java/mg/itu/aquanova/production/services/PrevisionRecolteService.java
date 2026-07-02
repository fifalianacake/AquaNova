package mg.itu.aquanova.production.services;

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
    private static final Double ZERO = 0.0;
    private static final Double SEUIL_PROCHE_RECOLTE = 0.90;

    private final LotRepository lotRepository;
    private final PeseRepository peseRepository;

    public PrevisionRecolteService(LotRepository lotRepository, PeseRepository peseRepository) {
        this.lotRepository = lotRepository;
        this.peseRepository = peseRepository;
    }

    public Double calculerCroissanceMoyenne(Long lotId) {
        LotModels lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Lot introuvable: " + lotId));
        return calculerCroissanceMoyenne(lot);
    }

    public Double calculerCroissanceMoyenne(LotModels lot) {
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

        return round3(
                (dernierePesee.getPoidsMoyen().doubleValue()
                        - premierePesee.getPoidsMoyen().doubleValue())
                        / nombreJours);
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

        if (result.getPoidsCible() == null || result.getPoidsCible() <= ZERO) {
            result.setAlerte("Poids cible non renseigné pour l'espèce.");
            return result;
        }
        if (result.getPoidsMoyenActuel() == null || result.getPoidsMoyenActuel() <= ZERO) {
            result.setAlerte("Poids moyen actuel non disponible.");
            return result;
        }

        result.setProcheRecolte(estProcheDuPoidsCible(result.getPoidsMoyenActuel(), result.getPoidsCible()));

        Double croissanceMoyenne = calculerCroissanceMoyenne(lot);
        result.setCroissanceMoyenneJournaliere(croissanceMoyenne);

        if (result.getPoidsMoyenActuel() >= result.getPoidsCible()) {
            result.setDateRecolteEstimee(LocalDate.now());
            result.setAlerte("Poids cible atteint ou dépassé.");
            return result;
        }
        if (croissanceMoyenne == null) {
            result.setAlerte("Pas assez de pesées pour estimer la date de récolte.");
            return result;
        }
        if (croissanceMoyenne <= ZERO) {
            result.setAlerte("Croissance moyenne insuffisante ou négative.");
            return result;
        }

        Double poidsRestant = result.getPoidsCible() - result.getPoidsMoyenActuel();
        long joursRestants = (long) Math.ceil(poidsRestant / croissanceMoyenne);
        result.setDateRecolteEstimee(LocalDate.now().plusDays(joursRestants));
        return result;
    }

    private Double resolvePoidsMoyenActuel(LotModels lot) {
        if (lot.getPoidsMoyenActuel() != null) {
            return lot.getPoidsMoyenActuel();
        }

        List<Pese> pesees = peseRepository.findByLotIdOrderByDatePeseeDesc(lot.getId());
        if (pesees.isEmpty()) {
            return null;
        }
        return pesees.get(0).getPoidsMoyen().doubleValue();
    }

    private Double resolvePoidsCible(LotModels lot) {
        if (lot.getEspece() == null) {
            return null;
        }
        return lot.getEspece().getPoidsCibleMoyen() != null
                ? lot.getEspece().getPoidsCibleMoyen().doubleValue()
                : null;
    }

    private boolean estProcheDuPoidsCible(Double poidsMoyenActuel, Double poidsCible) {
        return poidsMoyenActuel >= poidsCible * SEUIL_PROCHE_RECOLTE;
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

    private Double round3(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
