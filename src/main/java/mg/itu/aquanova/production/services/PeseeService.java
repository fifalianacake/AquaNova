package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.repositories.PeseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PeseeService {

    private final PeseRepository peseeRepository;

    public PeseeService(PeseRepository peseeRepository) {
        this.peseeRepository = peseeRepository;
    }

    public List<Pese> listerToutesLesPesees() {
        return peseeRepository.findAll();
    }

    public List<Pese> listerPeseesParLot(Long idLot) {
        return peseeRepository.findByIdLotOrderByDatePeseeDesc(idLot);
    }

    public Optional<Pese> trouverParId(Long id) {
        return peseeRepository.findById(id);
    }

    // CREATE
    @Transactional
    public Pese enregistrerPesee(Long idLot, LocalDate datePesee, Integer nbEchantillon, BigDecimal poidsTotal, String observation) {
        if (idLot == null) throw new IllegalArgumentException("La pesée doit être rattachée à un lot.");
        if (nbEchantillon == null || nbEchantillon <= 0) throw new IllegalArgumentException("Le nombre d'échantillons doit être supérieur à 0.");
        if (poidsTotal == null || poidsTotal.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Le poids total doit être supérieur à 0.");

        BigDecimal poidsMoyen = poidsTotal.divide(BigDecimal.valueOf(nbEchantillon), 3, RoundingMode.HALF_UP);

        Pese pesee = new Pese();
        pesee.setIdLot(idLot);
        pesee.setDatePesee(datePesee != null ? datePesee : LocalDate.now());
        pesee.setNbEchantillon(nbEchantillon);
        pesee.setPoidsTotalEchantillon(poidsTotal);
        pesee.setPoidsMoyen(poidsMoyen);
        pesee.setObservation(observation);

        return peseeRepository.save(pesee);
    }

    // UPDATE
    @Transactional
    public Pese modifierPesee(Long id, LocalDate datePesee, Integer nbEchantillon, BigDecimal poidsTotal, String observation) {
        Pese pesee = peseeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesée introuvable avec l'ID : " + id));

        if (datePesee != null) pesee.setDatePesee(datePesee);
        if (observation != null) pesee.setObservation(observation);
        
        if (nbEchantillon != null) {
            if (nbEchantillon <= 0) throw new IllegalArgumentException("L'échantillon doit être supérieur à 0.");
            pesee.setNbEchantillon(nbEchantillon);
        }
        if (poidsTotal != null) {
            if (poidsTotal.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Le poids doit être supérieur à 0.");
            pesee.setPoidsTotalEchantillon(poidsTotal);
        }

        // Recalcul du poids moyen mis à jour
        BigDecimal poidsMoyen = pesee.getPoidsTotalEchantillon().divide(BigDecimal.valueOf(pesee.getNbEchantillon()), 3, RoundingMode.HALF_UP);
        pesee.setPoidsMoyen(poidsMoyen);

        return peseeRepository.save(pesee);
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
public BigDecimal getDernierPoidsMoyen(Long idLot) {
    List<Pese> historique = this.peseeRepository.findByIdLotOrderByDatePeseeDesc(idLot);
    if (historique.isEmpty()) return null;
    return historique.get(0).getPoidsMoyen();
}
}
