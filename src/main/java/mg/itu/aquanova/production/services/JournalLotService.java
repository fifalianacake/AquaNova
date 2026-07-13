package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.JournalLotRepository;
import mg.itu.aquanova.production.repositories.TypeEvenementLotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalLotService {
    private final JournalLotRepository journalLotRepository;
    private final TypeEvenementLotRepository typeEvenementLotRepository;

    public JournalLotService(JournalLotRepository journalLotRepository, TypeEvenementLotRepository typeEvenementLotRepository) {
        this.journalLotRepository = journalLotRepository;
        this.typeEvenementLotRepository = typeEvenementLotRepository;
    }

    public void inscrireEvenement(LotModels lot, TypeEvenementLot.LibelleEvenement typeEvt, String description) {
        inscrireEvenement(lot, typeEvt, description, LocalDateTime.now());
    }

    public void inscrireEvenement(LotModels lot, TypeEvenementLot.LibelleEvenement typeEvt, String description, LocalDate dateEvenement) {
        inscrireEvenement(lot, typeEvt, description, dateEvenement.atStartOfDay());
    }

    public void inscrireEvenement(LotModels lot, TypeEvenementLot.LibelleEvenement typeEvt, String description, LocalDateTime dateEvenement) {
        TypeEvenementLot type = typeEvenementLotRepository.findByLibelle(typeEvt)
                .orElseThrow(() -> new RuntimeException("Type d'événement introuvable"));
        JournalLot journal = new JournalLot();
        journal.setLot(lot);
        journal.setTypeEvenement(type);
        journal.setDateEvenement(dateEvenement);
        journal.setDescription(description);
        journalLotRepository.save(journal);
    }

    public List<JournalLot> listerTous() { return journalLotRepository.findAllByOrderByDateEvenementAsc(); }
    public List<JournalLot> obtenirJournalParLot(Long lotId) { return journalLotRepository.findByLotIdOrderByDateEvenementAsc(lotId); }

    public Page<JournalLot> lister(JournalLotFilter filter, Pageable pageable) {
        return journalLotRepository.findAll(specification(filter), pageable);
    }

    private Specification<JournalLot> specification(JournalLotFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getLotId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("lot").get("id"), filter.getLotId()));
            }
            if (filter.getTypeEvenement() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("typeEvenement").get("libelle"), filter.getTypeEvenement()));
            }
            if (filter.getDateDebut() != null) {
                LocalDateTime dateDebut = filter.getDateDebut().atStartOfDay();
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateEvenement"), dateDebut));
            }
            if (filter.getDateFin() != null) {
                LocalDateTime dateFin = filter.getDateFin().atTime(LocalTime.MAX);
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateEvenement"), dateFin));
            }

            return predicates;
        };
    }
}
