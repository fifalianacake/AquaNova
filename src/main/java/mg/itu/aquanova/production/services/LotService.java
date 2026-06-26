package mg.itu.aquanova.production.services;

import java.util.List;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;

@Service
public class LotService {

    private final LotRepository repository;

    public LotService(LotRepository repository) {
        this.repository = repository;
    }

    public List<LotModels> listerTous() {
        return repository.findAll();
    }

    public List<LotModels> lister(LotFilter filter) {
        java.util.List<LotModels> lots = repository.findAll();
        java.util.stream.Stream<LotModels> stream = lots.stream();

        if (filter == null) return lots;

        if (filter.getId() != null) {
            stream = stream.filter(l -> l.getId() != null && l.getId().equals(filter.getId()));
        }
        if (filter.getCode() != null && !filter.getCode().isBlank()) {
            String lower = filter.getCode().toLowerCase();
            stream = stream.filter(l -> l.getCode() != null && l.getCode().toLowerCase().contains(lower));
        }
        if (filter.getEspeceId() != null) {
            stream = stream.filter(l -> l.getEspece() != null && l.getEspece().getId() != null && l.getEspece().getId().equals(filter.getEspeceId()));
        }
        if (filter.getBassinId() != null) {
            stream = stream.filter(l -> l.getBassin() != null && l.getBassin().getId() != null && l.getBassin().getId().equals(filter.getBassinId()));
        }
        if (filter.getStadeId() != null) {
            stream = stream.filter(l -> l.getStadeCroissance() != null && l.getStadeCroissance().getId() != null && l.getStadeCroissance().getId().equals(filter.getStadeId()));
        }
        if (filter.getStatutId() != null) {
            stream = stream.filter(l -> l.getStatutLot() != null && l.getStatutLot().getId() != null && l.getStatutLot().getId().equals(filter.getStatutId()));
        }

        java.time.LocalDate fromDate = null;
        java.time.LocalDate toDate = null;
        try {
            if (filter.getDateFrom() != null && !filter.getDateFrom().isBlank()) fromDate = java.time.LocalDate.parse(filter.getDateFrom());
        } catch (java.time.format.DateTimeParseException ex) {
            fromDate = null;
        }
        try {
            if (filter.getDateTo() != null && !filter.getDateTo().isBlank()) toDate = java.time.LocalDate.parse(filter.getDateTo());
        } catch (java.time.format.DateTimeParseException ex) {
            toDate = null;
        }
        if (fromDate != null) {
            java.time.LocalDate fd = fromDate;
            stream = stream.filter(l -> l.getDateMiseEnCharge() != null && !l.getDateMiseEnCharge().isBefore(fd));
        }
        if (toDate != null) {
            java.time.LocalDate td = toDate;
            stream = stream.filter(l -> l.getDateMiseEnCharge() != null && !l.getDateMiseEnCharge().isAfter(td));
        }
        if (filter.getEffectifMin() != null) {
            stream = stream.filter(l -> l.getEffectifActuel() != null && l.getEffectifActuel() >= filter.getEffectifMin());
        }
        if (filter.getEffectifMax() != null) {
            stream = stream.filter(l -> l.getEffectifActuel() != null && l.getEffectifActuel() <= filter.getEffectifMax());
        }

        return stream.toList();
    }

    public LotModels trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + id));
    }

    public LotModels creer(LotModels lot) {
        return repository.save(lot);
    }

    public LotModels modifier(Long id, LotModels lot) {
        LotModels exist = trouverParId(id);
        exist.setCode(lot.getCode());
        exist.setEspece(lot.getEspece());
        exist.setBassin(lot.getBassin());
        exist.setStadeCroissance(lot.getStadeCroissance());
        exist.setStatutLot(lot.getStatutLot());
        exist.setDateMiseEnCharge(lot.getDateMiseEnCharge());
        exist.setEffectifInitial(lot.getEffectifInitial());
        exist.setEffectifActuel(lot.getEffectifActuel());
        exist.setPoidsMoyenInitial(lot.getPoidsMoyenInitial());
        exist.setPoidsMoyenActuel(lot.getPoidsMoyenActuel());
        exist.setObservation(lot.getObservation());
        return repository.save(exist);
    }

    public void supprimer(Long id) {
        LotModels l = trouverParId(id);
        repository.delete(l);
    }
}
