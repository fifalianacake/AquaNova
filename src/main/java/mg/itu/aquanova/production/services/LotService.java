package mg.itu.aquanova.production.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<LotModels> lister(LotFilter filter, Pageable pageable) {
        java.util.List<LotModels> lots = repository.findAll();
        java.util.stream.Stream<LotModels> stream = lots.stream();

        if (filter != null) {
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
        }
        
        List<LotModels> resultatFiltre = stream.toList();

        // On calcule l'index de départ
        int start = (int) pageable.getOffset();
        
        // On calcule l'index de fin 
        int end = Math.min((start + pageable.getPageSize()), resultatFiltre.size());

        // Sécurité au cas où l'index de départ dépasse la taille de la liste
        List<LotModels> pageContenu = new ArrayList<>();
        if (start <= resultatFiltre.size()) {
            pageContenu = resultatFiltre.subList(start, end);
        }

        // (avec la sous-liste, les infos de pagination, et la taille totale)
        return new PageImpl<>(pageContenu, pageable, resultatFiltre.size());
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
