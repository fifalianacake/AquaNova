package mg.itu.aquanova.production.services;

import mg.itu.aquanova.production.models.JournalLot;
import mg.itu.aquanova.production.models.Lot;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.JournalLotRepository;
import mg.itu.aquanova.production.repositories.TypeEvenementLotRepository;
import org.springframework.stereotype.Service;
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

    public void inscrireEvenement(Lot lot, TypeEvenementLot.LibelleEvenement typeEvt, String description) {
        TypeEvenementLot type = typeEvenementLotRepository.findByLibelle(typeEvt)
                .orElseThrow(() -> new RuntimeException("Type d'événement introuvable"));
        JournalLot journal = new JournalLot();
        journal.setLot(lot);
        journal.setTypeEvenement(type);
        journal.setDateEvenement(LocalDateTime.now());
        journal.setDescription(description);
        journalLotRepository.save(journal);
    }

    public List<JournalLot> listerTous() { return journalLotRepository.findAll(); }
    public List<JournalLot> obtenirJournalParLot(Long lotId) { return journalLotRepository.findByLotIdOrderByDateEvenementDesc(lotId); }
    public void supprimer(Long id) { journalLotRepository.deleteById(id); }
}