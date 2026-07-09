package mg.itu.aquanova.production.services;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.dto.PeseeFilter;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.PeseRepository;
import mg.itu.aquanova.referentiel.repositories.StadeCroissanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PeseeService {

    private final PeseRepository peseeRepository;
    private final LotRepository lotRepository;
    private final JournalLotService journalLotService;
    private final StadeCroissanceRepository stadeCroissanceRepository;

    public PeseeService(PeseRepository peseeRepository, LotRepository lotRepository, JournalLotService journalLotService,
            StadeCroissanceRepository stadeCroissanceRepository) {
        this.peseeRepository = peseeRepository;
        this.lotRepository = lotRepository;
        this.journalLotService = journalLotService;
        this.stadeCroissanceRepository = stadeCroissanceRepository;
    }

    public List<Pese> listerToutesLesPesees() {
        return peseeRepository.findAll();
    }

    public List<Pese> listerPeseesParLot(Long idLot) {
        return peseeRepository.findByLotIdOrderByDatePeseeDesc(idLot);
    }

    public Page<Pese> lister(Long idLot, PeseeFilter filter, Pageable pageable) {
        return peseeRepository.findAll(specification(idLot, filter), pageable);
    }

    private Specification<Pese> specification(Long idLot, PeseeFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.equal(root.get("lot").get("id"), idLot);

            if (filter != null) {
                if (filter.getDateDebut() != null) {
                    predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("datePesee"), filter.getDateDebut()));
                }
                if (filter.getDateFin() != null) {
                    predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("datePesee"), filter.getDateFin()));
                }
            }

            return predicates;
        };
    }

    public Optional<Pese> trouverParId(Long id) {
        return peseeRepository.findById(id);
    }

    // CREATE
    @Transactional
    public Pese enregistrerPesee(Long idLot, LocalDate datePesee, Integer nbEchantillon, Double poidsTotal, String observation) {
        LotModels lot = validerEtTrouverLot(idLot, datePesee);
        BigDecimal poidsTotalDecimal = poidsTotal != null ? BigDecimal.valueOf(poidsTotal) : null;
        validerMesures(nbEchantillon, poidsTotalDecimal);
        BigDecimal poidsMoyen = BigDecimal.valueOf(round3(poidsTotal / nbEchantillon));

        Pese pesee = new Pese();
        pesee.setLot(lot);
        pesee.setDatePesee(datePesee);
        pesee.setNbEchantillon(nbEchantillon);
        pesee.setPoidsTotalEchantillon(poidsTotalDecimal);
        pesee.setPoidsMoyen(poidsMoyen);
        pesee.setObservation(observation);

        Pese saved = peseeRepository.save(pesee);
        recalculerPoidsMoyenActuel(lot);

        journalLotService.inscrireEvenement(
                lot,
                TypeEvenementLot.LibelleEvenement.PESEE,
                "Pesée de " + nbEchantillon
                        + " échantillons, poids total " + poidsTotal
                        + ", poids moyen " + poidsMoyen,
                datePesee);

        return saved;
    }

    // UPDATE
    @Transactional
    public Pese modifierPesee(Long id, LocalDate datePesee, Integer nbEchantillon, Double poidsTotal, String observation) {
        Pese pesee = peseeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesée introuvable avec l'ID : " + id));

        LocalDate nouvelleDate = datePesee != null ? datePesee : pesee.getDatePesee();
        Integer nouveauNbEchantillon = nbEchantillon != null ? nbEchantillon : pesee.getNbEchantillon();
        BigDecimal nouveauPoidsTotal = poidsTotal != null
                ? BigDecimal.valueOf(poidsTotal)
                : pesee.getPoidsTotalEchantillon();

        LotModels lot = validerEtTrouverLot(pesee.getLot(), nouvelleDate);
        validerMesures(nouveauNbEchantillon, nouveauPoidsTotal);

        pesee.setDatePesee(nouvelleDate);
        if (observation != null) pesee.setObservation(observation);
        pesee.setNbEchantillon(nouveauNbEchantillon);
        pesee.setPoidsTotalEchantillon(nouveauPoidsTotal);

        BigDecimal poidsMoyen = BigDecimal.valueOf(
                round3(pesee.getPoidsTotalEchantillon().doubleValue() / pesee.getNbEchantillon()));
        pesee.setPoidsMoyen(poidsMoyen);

        Pese saved = peseeRepository.save(pesee);
        recalculerPoidsMoyenActuel(lot);

        return saved;
    }

    // DELETE
    @Transactional
    public void supprimerPesee(Long id) {
        Pese pesee = peseeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impossible de supprimer : Pesée introuvable."));
        LotModels lot = pesee.getLot();
        peseeRepository.delete(pesee);
        recalculerPoidsMoyenActuel(lot);
    }



// Aider module Lot
public Double getDernierPoidsMoyen(Long idLot) {
    List<Pese> historique = this.peseeRepository.findByLotIdOrderByDatePeseeDesc(idLot);
    if (historique.isEmpty()) return null;
    return historique.get(0).getPoidsMoyen().doubleValue();
}

    private LotModels validerEtTrouverLot(Long idLot, LocalDate datePesee) {
        if (idLot == null) {
            throw new IllegalArgumentException("La pesée doit être rattachée à un lot.");
        }
        if (datePesee == null) {
            throw new IllegalArgumentException("La date de pesée est obligatoire.");
        }

        LotModels lot = lotRepository.findById(idLot)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + idLot));

        if (lot.getStatutLot() != null
                && (lot.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE
                        || lot.getStatutLot().getLibelle() == StatutLotEnum.ANNULE)) {
            throw new IllegalStateException("Impossible d'enregistrer une pesée sur un lot clôturé ou annulé.");
        }
        if (lot.getDateMiseEnCharge() != null && datePesee.isBefore(lot.getDateMiseEnCharge())) {
            throw new IllegalArgumentException("La date de pesée ne peut pas être antérieure à la date de mise en charge du lot.");
        }

        return lot;
    }

    private LotModels validerEtTrouverLot(LotModels lot, LocalDate datePesee) {
        if (lot == null || lot.getId() == null) {
            throw new IllegalArgumentException("La pesée doit être rattachée à un lot.");
        }
        return validerEtTrouverLot(lot.getId(), datePesee);
    }

    private void validerMesures(Integer nbEchantillon, BigDecimal poidsTotal) {
        if (nbEchantillon == null || nbEchantillon <= 0) {
            throw new IllegalArgumentException("Le nombre d'échantillons doit être supérieur à 0.");
        }
        if (poidsTotal == null || poidsTotal.signum() <= 0) {
            throw new IllegalArgumentException("Le poids total de l'échantillon doit être supérieur à 0.");
        }
    }

    private void recalculerPoidsMoyenActuel(LotModels lot) {
        if (lot == null || lot.getId() == null) {
            return;
        }

        List<Pese> historique = peseeRepository.findByLotIdOrderByDatePeseeDesc(lot.getId());
        Double poidsActuel = historique.isEmpty()
                ? lot.getPoidsMoyenInitial()
                : historique.get(0).getPoidsMoyen().doubleValue();

        lot.setPoidsMoyenActuel(poidsActuel);

        if (poidsActuel != null) {
            BigDecimal poidsDecimal = BigDecimal.valueOf(poidsActuel);
            stadeCroissanceRepository.findAll().stream()
                    .filter(stade -> stade.correspondAuPoids(poidsDecimal))
                    .findFirst()
                    .ifPresent(lot::setStadeCroissance);
        }

        lotRepository.save(lot);
    }

    private Double round3(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
