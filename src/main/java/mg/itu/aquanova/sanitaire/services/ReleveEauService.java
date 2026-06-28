package mg.itu.aquanova.sanitaire.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.sanitaire.models.ReleveEau;
import mg.itu.aquanova.sanitaire.repositories.ReleveEauRepository;

@Service
public class ReleveEauService {

    @Autowired
    private ReleveEauRepository repo;

    // ==========================
    // CRUD
    // ==========================

    public ReleveEau create(ReleveEau releve) {
        validate(releve);
        return repo.save(releve);
    }

    public ReleveEau update(ReleveEau releve) {
        validate(releve);
        return repo.save(releve);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public ReleveEau findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<ReleveEau> findAll() {
        return repo.findAll();
    }

    public ReleveEau getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // ==========================
    // SEARCH
    // ==========================

    public List<ReleveEau> search(Long id,
            String bassin,
            String start,
            String end,
            Double minTemp,
            Double maxTemp,
            Double minPh,
            Double maxPh,
            Double minOxy,
            Double maxOxy) {

        LocalDate s = (start != null && !start.isBlank())
                ? LocalDate.parse(start)
                : null;

        LocalDate e = (end != null && !end.isBlank())
                ? LocalDate.parse(end)
                : null;

        return repo.findAll().stream()

                .filter(r -> id == null || r.getId().equals(id))

                .filter(r -> bassin == null
                        || bassin.isBlank()
                        || r.getBassin().getReference().toLowerCase()
                                .contains(bassin.toLowerCase()))

                .filter(r -> s == null || !r.getDateReleve().isBefore(s))
                .filter(r -> e == null || !r.getDateReleve().isAfter(e))

                // TEMP INTERVAL
                .filter(r -> minTemp == null || r.getTemperature() >= minTemp)
                .filter(r -> maxTemp == null || r.getTemperature() <= maxTemp)

                // PH INTERVAL
                .filter(r -> minPh == null || r.getPh() >= minPh)
                .filter(r -> maxPh == null || r.getPh() <= maxPh)

                // OXYGEN INTERVAL
                .filter(r -> minOxy == null || r.getOxygene() >= minOxy)
                .filter(r -> maxOxy == null || r.getOxygene() <= maxOxy)

                .toList();
    }

    // ==========================
    // BASSIN
    // ==========================

    public List<ReleveEau> getByBassin(Long bassinId) {

        return repo.findByBassinId(bassinId);
    }

    public List<ReleveEau> getRecentByBassin(Long bassinId) {

        return repo.findByBassinId(bassinId)
                .stream()
                .sorted(
                        Comparator.comparing(ReleveEau::getDateReleve)
                                .reversed())
                .limit(10)
                .toList();
    }

    // ==========================
    // ALERTES
    // ==========================

    public boolean verifierSeuilsQualiteEau(ReleveEau releve) {

        if (releve.getTemperature() < 18)
            return true;

        if (releve.getTemperature() > 30)
            return true;

        if (releve.getPh() < 6.5)
            return true;

        if (releve.getPh() > 8.5)
            return true;

        if (releve.getOxygene() < 5)
            return true;

        return false;
    }

    // ==========================
    // VALIDATION
    // ==========================

    private void validate(ReleveEau releve) {

        if (releve.getBassin() == null)
            throw new RuntimeException("Le bassin est obligatoire.");

        if (releve.getUser() == null)
            throw new RuntimeException("L'utilisateur est obligatoire.");

        if (releve.getDateReleve() == null)
            throw new RuntimeException("La date est obligatoire.");

        if (releve.getTemperature() == null)
            throw new RuntimeException("La température est obligatoire.");

        if (releve.getPh() == null)
            throw new RuntimeException("Le pH est obligatoire.");

        if (releve.getOxygene() == null)
            throw new RuntimeException("L'oxygène est obligatoire.");

        if (releve.getTemperature() <= 0)
            throw new RuntimeException("La température doit être supérieure à 0.");

        if (releve.getPh() < 0 || releve.getPh() > 14)
            throw new RuntimeException("Le pH doit être compris entre 0 et 14.");

        if (releve.getOxygene() < 0)
            throw new RuntimeException("L'oxygène ne peut pas être négatif.");
    }

}