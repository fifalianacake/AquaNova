package mg.itu.aquanova.alimentation.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.alimentation.models.*;
import mg.itu.aquanova.alimentation.repositories.MouvementStockRepository;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class MouvementService {

    @Autowired
    private MouvementStockRepository repo;

    @Autowired
    private AlimentRepository alimentRepository;

    public MouvementStock create(MouvementStock m) {

        validate(m);

        List<MouvementStock> all = repo.findByAlimentId(m.getAliment().getId());

        // simulate insertion
        all.add(m);

        // validate FULL timeline (past + future)
        validateTimelineList(all);

        return repo.save(m);
    }

    private void checkFIFOAvailability(MouvementStock m) {

        LocalDate date = m.getDateMouvement();
        Long alimentId = m.getAliment().getId();

        Double stockAtDate = repo.findByAlimentId(alimentId)
                .stream()
                // ONLY movements before or equal to the date
                .filter(x -> !x.getDateMouvement().isAfter(date))
                .mapToDouble(x -> {
                    if (x.getTypeMouvement() == TypeMouvement.ENTREE)
                        return x.getQuantite();
                    else
                        return -x.getQuantite();
                })
                .sum();

        if (stockAtDate < m.getQuantite()) {
            throw new RuntimeException(
                    "Stock insuffisant à la date " + date +
                            " | Disponible: " + stockAtDate +
                            " | Demandé: " + m.getQuantite());
        }
    }

    private void applyFIFO(MouvementStock m) {

        Double remaining = m.getQuantite();

        List<MouvementStock> entries = repo
                .findByAlimentId(m.getAliment().getId())
                .stream()
                .filter(x -> x.getTypeMouvement() == TypeMouvement.ENTREE)
                .sorted(Comparator.comparing(MouvementStock::getDateMouvement))
                .toList();

        for (MouvementStock entry : entries) {

            if (remaining <= 0)
                break;

            Double available = entry.getQuantite();

            if (available <= 0)
                continue;

            Double taken = Math.min(available, remaining);

            // simulate consumption
            entry.setQuantite(available - taken);
            repo.save(entry);

            remaining -= taken;
        }

        if (remaining > 0) {
            throw new RuntimeException("Stock insuffisant, reste: " + remaining);
        }
    }

    private void validateTimelineList(List<MouvementStock> list) {

        list = list.stream()
                .sorted(Comparator.comparing(MouvementStock::getDateMouvement))
                .toList();

        double stock = 0;

        for (MouvementStock m : list) {

            if (m.getTypeMouvement() == TypeMouvement.ENTREE)
                stock += m.getQuantite();
            else
                stock -= m.getQuantite();

            if (stock < 0) {
                throw new RuntimeException(
                        "Stock devient négatif à la date "
                                + m.getDateMouvement());
            }
        }
    }

    public MouvementStock update(MouvementStock m) {

        validate(m);
        validateUpdate(m);

        MouvementStock existing = findById(m.getId());
        if (existing.getDistribution() != null) {
            throw new IllegalStateException(
                    "Ce mouvement provient de la distribution #" + existing.getDistribution().getId()
                            + " : modifiez cette distribution plutôt que le mouvement directement.");
        }

        List<MouvementStock> all = repo.findByAlimentId(m.getAliment().getId());

        // replace the edited movement in memory
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(m.getId())) {
                all.set(i, m);
                break;
            }
        }

        validateTimelineList(all);

        return repo.save(m);
    }

    private void checkStockAtDateExcludingSelf(MouvementStock m) {

        LocalDate date = m.getDateMouvement();
        Long alimentId = m.getAliment().getId();

        Double stockAtDate = repo.findByAlimentId(alimentId)
                .stream()
                .filter(x -> !x.getId().equals(m.getId()))
                .filter(x -> !x.getDateMouvement().isAfter(date))
                .mapToDouble(x -> {
                    if (x.getTypeMouvement() == TypeMouvement.ENTREE)
                        return x.getQuantite();
                    else
                        return -x.getQuantite();
                })
                .sum();

        if (stockAtDate < m.getQuantite()) {
            throw new RuntimeException(
                    "Stock insuffisant à la date " + date +
                            " | Disponible: " + stockAtDate +
                            " | Demandé: " + m.getQuantite());
        }
    }

    public void delete(Long id) {

        MouvementStock toDelete = findById(id);

        if (toDelete.getDistribution() != null) {
            throw new IllegalStateException(
                    "Ce mouvement provient de la distribution #" + toDelete.getDistribution().getId()
                            + " : supprimez cette distribution plutôt que le mouvement directement.");
        }

        deleteInternal(toDelete);
    }

    public void deleteLinkedToDistribution(Long id) {
        deleteInternal(findById(id));
    }

    private void deleteInternal(MouvementStock toDelete) {

        List<MouvementStock> all = repo.findByAlimentId(toDelete.getAliment().getId());

        all.removeIf(m -> m.getId().equals(toDelete.getId()));

        validateTimelineList(all);

        repo.deleteById(toDelete.getId());
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

        Double stock = 0.0;

        for (MouvementStock m : repo.findByAlimentId(alimentId)) {

            if (m.getTypeMouvement() == TypeMouvement.ENTREE)
                stock += m.getQuantite();
            else
                stock -= m.getQuantite();
        }

        return stock;
    }

    private void validate(MouvementStock m) {

        if (m == null) {
            throw new IllegalArgumentException("Le mouvement est obligatoire");
        }

        if (m.getDateMouvement() == null) {
            throw new IllegalArgumentException("La date du mouvement est obligatoire");
        }

        if (m.getAliment() == null || m.getAliment().getId() == null) {
            throw new IllegalArgumentException("L'aliment est obligatoire");
        }

        if (!alimentRepository.existsById(m.getAliment().getId())) {
            throw new IllegalArgumentException("Aliment introuvable : " + m.getAliment().getId());
        }

        if (m.getTypeMouvement() == null) {
            throw new IllegalArgumentException("Le type de mouvement est obligatoire");
        }

        if (m.getQuantite() == null || m.getQuantite() <= 0
                || m.getQuantite().isNaN() || m.getQuantite().isInfinite()) {
            throw new IllegalArgumentException("Quantité invalide");
        }
    }

    private void validateUpdate(MouvementStock m) {
        if (m.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du mouvement est obligatoire pour la modification");
        }

        if (!repo.existsById(m.getId())) {
            throw new IllegalArgumentException("Mouvement introuvable : " + m.getId());
        }
    }

    public MouvementStock findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement introuvable"));
    }

    public java.util.Optional<MouvementStock> findByDistributionId(Long distributionId) {
        return repo.findByDistributionId(distributionId);
    }
}
