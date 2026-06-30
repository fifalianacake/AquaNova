package mg.itu.aquanova.stock.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.stock.models.*;
import mg.itu.aquanova.stock.repositories.MouvementLotRepository;
import mg.itu.aquanova.stock.repositories.MouvementStockRepository;
import mg.itu.aquanova.stock.repositories.StockLotRepository;

@Service
public class MouvementService {

    @Autowired
    private MouvementStockRepository repo;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockLotRepository lotRepo;

    @Autowired
    private MouvementLotRepository mouvementLotRepo;

    // public MouvementStock create(MouvementStock m) {
    // validate(m);
    // return repo.save(m);
    // }

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
                .filter(m -> id == null || m.getId().equals(id))
                .filter(m -> t == null || m.getTypeMouvement() == t)
                .filter(m -> aliment == null
                        || aliment.isBlank()
                        || m.getAliment().getNom().toLowerCase()
                                .contains(aliment.toLowerCase()))
                .filter(m -> s == null || !m.getDateMouvement().isBefore(s))
                .filter(m -> e == null || !m.getDateMouvement().isAfter(e))
                .toList();
    }

    // private void validate(MouvementStock m) {

    // if (m.getQuantite() <= 0)
    // throw new RuntimeException("Quantité invalide");

    // if (m.getTypeMouvement() == TypeMouvement.SORTIE) {
    // Double stock = stockService.getStockAtDate(
    // m.getAliment().getId(),
    // m.getDateMouvement());

    // if (stock < m.getQuantite())
    // throw new RuntimeException("Stock insuffisant");
    // }
    // }

    public Double getStock(Long alimentId) {
        return lotRepo
                .findByAlimentIdAndQuantiteRestanteGreaterThanOrderByDateEntreeAsc(
                        alimentId, 0.0)
                .stream()
                .mapToDouble(StockLot::getQuantiteRestante)
                .sum();
    }

    public MouvementStock findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }

    private void handleEntree(MouvementStock m) {

        StockLot lot = new StockLot();
        lot.setAliment(m.getAliment());
        lot.setQuantiteInitiale(m.getQuantite());
        lot.setQuantiteRestante(m.getQuantite());
        lot.setDateEntree(m.getDateMouvement());

        lotRepo.save(lot);

        // link movement ↔ lot
        MouvementLot ml = new MouvementLot();
        ml.setMouvement(m);
        ml.setLot(lot);
        ml.setQuantite(m.getQuantite());

        mouvementLotRepo.save(ml);
    }

    private void handleSortie(MouvementStock m) {

        double remaining = m.getQuantite();

        var lots = lotRepo
                .findByAlimentIdAndQuantiteRestanteGreaterThanOrderByDateEntreeAsc(
                        m.getAliment().getId(), 0.0);

        for (StockLot lot : lots) {

            if (remaining <= 0)
                break;

            double available = lot.getQuantiteRestante();
            double taken = Math.min(available, remaining);

            lot.setQuantiteRestante(available - taken);
            lotRepo.save(lot);

            MouvementLot ml = new MouvementLot();
            ml.setMouvement(m);
            ml.setLot(lot);
            ml.setQuantite(taken);

            mouvementLotRepo.save(ml);

            remaining -= taken;
        }

        if (remaining > 0) {
            throw new RuntimeException("Stock insuffisant (FIFO)");
        }
    }

    private void validate(MouvementStock m) {

        if (m.getQuantite() == null || m.getQuantite() <= 0)
            throw new RuntimeException("Quantité invalide");

        if (m.getTypeMouvement() == TypeMouvement.SORTIE
                || m.getTypeMouvement() == TypeMouvement.PERTE) {

            double total = lotRepo
                    .findByAlimentIdAndQuantiteRestanteGreaterThanOrderByDateEntreeAsc(
                            m.getAliment().getId(), 0.0)
                    .stream()
                    .mapToDouble(StockLot::getQuantiteRestante)
                    .sum();

            if (m.getQuantite() > total) {
                throw new RuntimeException("Stock insuffisant");
            }
        }
    }

    public MouvementStock create(MouvementStock m) {

        validate(m);

        MouvementStock saved = repo.save(m);

        if (m.getTypeMouvement() == TypeMouvement.ENTREE) {
            handleEntree(saved);
        }

        if (m.getTypeMouvement() == TypeMouvement.SORTIE
                || m.getTypeMouvement() == TypeMouvement.PERTE) {
            handleSortie(saved);
        }

        return saved;
    }
}