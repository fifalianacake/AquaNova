package mg.itu.aquanova.stock.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.stock.models.*;
import mg.itu.aquanova.stock.repositories.MouvementStockRepository;

@Service
public class MouvementService {

    @Autowired
    private MouvementStockRepository repo;

    @Autowired
    private StockService stockService;

    public MouvementStock create(MouvementStock m) {
        validate(m);
        return repo.save(m);
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

    private void validate(MouvementStock m) {

        if (m.getQuantite() <= 0)
            throw new RuntimeException("Quantité invalide");

        if (m.getTypeMouvement() == TypeMouvement.SORTIE) {
            Double stock = stockService.getStockAtDate(
                    m.getAliment().getId(),
                    m.getDateMouvement());

            if (stock < m.getQuantite())
                throw new RuntimeException("Stock insuffisant");
        }
    }

    public MouvementStock findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }
}