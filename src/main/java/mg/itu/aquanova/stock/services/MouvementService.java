package mg.itu.aquanova.stock.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.stock.models.*;
import mg.itu.aquanova.stock.repositories.MouvementStockRepository;

@Service
public class MouvementService {

    @Autowired
    private MouvementStockRepository repo;

    public MouvementStock create(MouvementStock m) {

        validate(m);

        if (m.getTypeMouvement() != TypeMouvement.ENTREE) {
            checkFIFOAvailability(m);
        }

        return repo.save(m);
    }

    private void checkFIFOAvailability(MouvementStock m) {

        double remaining = m.getQuantite();

        List<MouvementStock> entries = repo.findByAlimentId(m.getAliment().getId())
                .stream()
                .filter(x -> x.getTypeMouvement() == TypeMouvement.ENTREE)
                .sorted((a, b) -> a.getDateMouvement().compareTo(b.getDateMouvement()))
                .toList();

        double totalAvailable = entries.stream()
                .mapToDouble(MouvementStock::getQuantite)
                .sum();

        if (totalAvailable < remaining) {
            throw new RuntimeException("Stock insuffisant pour FIFO");
        }
    }

 
    private void applyFIFO(MouvementStock m) {

        double remaining = m.getQuantite();

        List<MouvementStock> entries = repo
                .findByAlimentId(m.getAliment().getId())
                .stream()
                .filter(x -> x.getTypeMouvement() == TypeMouvement.ENTREE)
                .sorted(Comparator.comparing(MouvementStock::getDateMouvement))
                .toList();

        for (MouvementStock entry : entries) {

            if (remaining <= 0)
                break;

            double available = entry.getQuantite();

            if (available <= 0)
                continue;

            double taken = Math.min(available, remaining);

            // simulate consumption
            entry.setQuantite(available - taken);
            repo.save(entry);

            remaining -= taken;
        }

        if (remaining > 0) {
            throw new RuntimeException("Stock insuffisant (FIFO)");
        }
    }


    public MouvementStock update(MouvementStock m) {
        validate(m);
        return repo.save(m);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<MouvementStock> search(Long id,
            String type,
            String aliment,
            String start,
            String end) {

        TypeMouvement t = (type != null && !type.isBlank())
                ? TypeMouvement.valueOf(type)
                : null;

        LocalDate s = (start != null && !start.isBlank())
                ? LocalDate.parse(start)
                : null;

        LocalDate e = (end != null && !end.isBlank())
                ? LocalDate.parse(end)
                : null;

        return repo.findAll().stream()
                .filter(mv -> id == null || mv.getId().equals(id))
                .filter(mv -> t == null || mv.getTypeMouvement() == t)
                .filter(mv -> aliment == null
                        || aliment.isBlank()
                        || mv.getAliment().getNom().toLowerCase()
                                .contains(aliment.toLowerCase()))
                .filter(mv -> s == null || !mv.getDateMouvement().isBefore(s))
                .filter(mv -> e == null || !mv.getDateMouvement().isAfter(e))
                .toList();
    }

   
    public Double getStock(Long alimentId) {

        double stock = 0;

        for (MouvementStock m : repo.findByAlimentId(alimentId)) {

            if (m.getTypeMouvement() == TypeMouvement.ENTREE)
                stock += m.getQuantite();
            else
                stock -= m.getQuantite();
        }

        return stock;
    }

    private void validate(MouvementStock m) {

        if (m.getQuantite() == null || m.getQuantite() <= 0)
            throw new RuntimeException("Quantité invalide");
    }

    public MouvementStock findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }
}