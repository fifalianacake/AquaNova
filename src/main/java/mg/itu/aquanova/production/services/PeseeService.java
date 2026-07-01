package mg.itu.aquanova.production.services;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.PeseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PeseeService {

    private final PeseRepository peseeRepository;
    private final LotRepository lotRepository;
    private final JournalLotService journalLotService;

    public PeseeService(PeseRepository peseeRepository, LotRepository lotRepository, JournalLotService journalLotService) {
        this.peseeRepository = peseeRepository;
        this.lotRepository = lotRepository;
        this.journalLotService = journalLotService;
    }

    public List<Pese> listerToutesLesPesees() {
        return peseeRepository.findAll();
    }

    public List<Pese> listerPeseesParLot(Long idLot) {
        return peseeRepository.findByLotIdOrderByDatePeseeDesc(idLot);
    }

    public Optional<Pese> trouverParId(Long id) {
        return peseeRepository.findById(id);
    }

    // CREATE
    @Transactional
    public Pese enregistrerPesee(Long idLot, LocalDate datePesee, Integer nbEchantillon, Double poidsTotal, String observation) {
        LotModels lot = validerEtTrouverLot(idLot, datePesee);
        validerMesures(nbEchantillon, poidsTotal);

        Double poidsMoyen = round3(poidsTotal / nbEchantillon);

        Pese pesee = new Pese();
        pesee.setLot(lot);
        pesee.setDatePesee(datePesee);
        pesee.setNbEchantillon(nbEchantillon);
        pesee.setPoidsTotalEchantillon(poidsTotal);
        pesee.setPoidsMoyen(poidsMoyen);
        pesee.setObservation(observation);

        Pese saved = peseeRepository.save(pesee);
        lot.setPoidsMoyenActuel(poidsMoyen);
        lotRepository.save(lot);

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
        Double nouveauPoidsTotal = poidsTotal != null ? poidsTotal : pesee.getPoidsTotalEchantillon();

        LotModels lot = validerEtTrouverLot(pesee.getLot(), nouvelleDate);
        validerMesures(nouveauNbEchantillon, nouveauPoidsTotal);

        pesee.setDatePesee(nouvelleDate);
        if (observation != null) pesee.setObservation(observation);
        pesee.setNbEchantillon(nouveauNbEchantillon);
        pesee.setPoidsTotalEchantillon(nouveauPoidsTotal);

        Double poidsMoyen = round3(pesee.getPoidsTotalEchantillon() / pesee.getNbEchantillon());
        pesee.setPoidsMoyen(poidsMoyen);

        Pese saved = peseeRepository.save(pesee);
        lot.setPoidsMoyenActuel(poidsMoyen);
        lotRepository.save(lot);

        return saved;
    }

    // DELETE
    @Transactional
    public void supprimerPesee(Long id) {
        if (!peseeRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : Pesée introuvable.");
        }
        peseeRepository.deleteById(id);
    }



// Aider module Lot
public Double getDernierPoidsMoyen(Long idLot) {
    List<Pese> historique = this.peseeRepository.findByLotIdOrderByDatePeseeDesc(idLot);
    if (historique.isEmpty()) return null;
    return historique.get(0).getPoidsMoyen();
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

        if (lot.getStatutLot() != null && lot.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE) {
            throw new IllegalStateException("Impossible d'enregistrer une pesée sur un lot clôturé.");
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

    private void validerMesures(Integer nbEchantillon, Double poidsTotal) {
        if (nbEchantillon == null || nbEchantillon <= 0) {
            throw new IllegalArgumentException("Le nombre d'échantillons doit être supérieur à 0.");
        }
        if (poidsTotal == null || poidsTotal <= 0) {
            throw new IllegalArgumentException("Le poids total de l'échantillon doit être supérieur à 0.");
        }
    }

    private Double round3(Double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
