package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.JournalLotRepository;
import mg.itu.aquanova.production.repositories.TypeEvenementLotRepository;
import org.springframework.stereotype.Service;
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
        TypeEvenementLot type = typeEvenementLotRepository.findByLibelle(typeEvt)
                .orElseThrow(() -> new RuntimeException("Type d'événement introuvable"));
        JournalLot journal = new JournalLot();
        journal.setLot(lot);
        journal.setTypeEvenement(type);
        journal.setDateEvenement(LocalDateTime.now());
        journal.setDescription(description);
        journalLotRepository.save(journal);
    }

    public List<JournalLot> listerTous() { return journalLotRepository.findAllByOrderByDateEvenementAsc(); }
    public List<JournalLot> obtenirJournalParLot(Long lotId) { return journalLotRepository.findByLotIdOrderByDateEvenementAsc(lotId); }
    public List<JournalLot> rechercher(JournalLotFilter filter) {
        if (filter == null) {
            return listerTous();
        }

        LocalDateTime dateDebut = filter.getDateDebut() != null
                ? filter.getDateDebut().atStartOfDay()
                : null;
        LocalDateTime dateFin = filter.getDateFin() != null
                ? filter.getDateFin().atTime(LocalTime.MAX)
                : null;

        return journalLotRepository.rechercher(
                filter.getLotId(),
                filter.getTypeEvenement(),
                dateDebut,
                dateFin);
    }
    public void supprimer(Long id) { journalLotRepository.deleteById(id); }
}
