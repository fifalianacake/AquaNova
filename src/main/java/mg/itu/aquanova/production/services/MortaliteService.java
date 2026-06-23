package mg.itu.aquanova.production.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.repositories.MortaliteRepository;

@Service
public class MortaliteService {

    private final MortaliteRepository repo;

    public MortaliteService(MortaliteRepository repo) {
        this.repo = repo;
    }

    public List<MortaliteModels> findAll() {
        return repo.findAll();
    }

    public MortaliteModels findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public List<MortaliteModels> findByLot(Integer idLot) {
        return repo.findByIdLotOrderByDateMortaliteDesc(idLot);
    }

    public List<MortaliteModels> findByLotAndPeriode(Integer idLot, LocalDate dateDebut, LocalDate dateFin) {
        return repo.findByIdLotAndDateMortaliteBetweenOrderByDateMortaliteDesc(idLot, dateDebut, dateFin);
    }

    public MortaliteModels save(MortaliteModels mortalite) {
        validerMortalite(mortalite);
        normaliserCause(mortalite);
        return repo.save(mortalite);
    }

    public MortaliteModels saveAvecControleEffectifInitial(MortaliteModels mortalite, Integer effectifInitial) {
        validerMortalite(mortalite);
        validerEffectifInitial(effectifInitial);

        int totalApresEnregistrement = totalMortsApresEnregistrement(mortalite);
        if (totalApresEnregistrement > effectifInitial) {
            throw new IllegalArgumentException("Le cumul des mortalites ne peut pas depasser l'effectif initial du lot.");
        }

        normaliserCause(mortalite);
        return repo.save(mortalite);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Integer getTotalMortsByLot(Integer idLot) {
        validerIdLot(idLot);
        return repo.sumNbMortsByIdLot(idLot);
    }

    public Integer calculerEffectifVivant(Integer idLot, Integer effectifInitial) {
        validerIdLot(idLot);
        validerEffectifInitial(effectifInitial);

        int effectifVivant = effectifInitial - getTotalMortsByLot(idLot);
        if (effectifVivant < 0) {
            throw new IllegalStateException("Le cumul des mortalites depasse l'effectif initial du lot.");
        }
        return effectifVivant;
    }

    public BigDecimal calculerTauxSurvie(Integer effectifVivant, Integer effectifInitial) {
        validerEffectifInitial(effectifInitial);
        if (effectifVivant == null || effectifVivant < 0) {
            throw new IllegalArgumentException("L'effectif vivant doit etre positif ou nul.");
        }
        if (effectifVivant > effectifInitial) {
            throw new IllegalArgumentException("L'effectif vivant ne peut pas depasser l'effectif initial.");
        }

        return BigDecimal.valueOf(effectifVivant)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(effectifInitial), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculerTauxSurvieByLot(Integer idLot, Integer effectifInitial) {
        Integer effectifVivant = calculerEffectifVivant(idLot, effectifInitial);
        return calculerTauxSurvie(effectifVivant, effectifInitial);
    }

    public BigDecimal calculerBiomasseActive(Integer effectifVivant, BigDecimal poidsMoyen) {
        if (effectifVivant == null || effectifVivant < 0) {
            throw new IllegalArgumentException("L'effectif vivant doit etre positif ou nul.");
        }
        if (poidsMoyen == null || poidsMoyen.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le poids moyen doit etre strictement positif.");
        }

        return poidsMoyen.multiply(BigDecimal.valueOf(effectifVivant));
    }

    public BigDecimal calculerBiomasseActiveByLot(Integer idLot, Integer effectifInitial, BigDecimal poidsMoyen) {
        Integer effectifVivant = calculerEffectifVivant(idLot, effectifInitial);
        return calculerBiomasseActive(effectifVivant, poidsMoyen);
    }

    private int totalMortsApresEnregistrement(MortaliteModels mortalite) {
        int total = getTotalMortsByLot(mortalite.getIdLot());

        if (mortalite.getId() != null) {
            MortaliteModels ancienneMortalite = findById(mortalite.getId());
            if (ancienneMortalite != null && ancienneMortalite.getNbMorts() != null) {
                total -= ancienneMortalite.getNbMorts();
            }
        }

        return total + mortalite.getNbMorts();
    }

    private void validerMortalite(MortaliteModels mortalite) {
        if (mortalite == null) {
            throw new IllegalArgumentException("La mortalite est obligatoire.");
        }
        validerIdLot(mortalite.getIdLot());
        if (mortalite.getDateMortalite() == null) {
            throw new IllegalArgumentException("La date de mortalite est obligatoire.");
        }
        if (mortalite.getNbMorts() == null || mortalite.getNbMorts() <= 0) {
            throw new IllegalArgumentException("Le nombre de morts doit etre strictement positif.");
        }
    }

    private void validerIdLot(Integer idLot) {
        if (idLot == null) {
            throw new IllegalArgumentException("La mortalite doit etre associee a un lot.");
        }
    }

    private void validerEffectifInitial(Integer effectifInitial) {
        if (effectifInitial == null || effectifInitial <= 0) {
            throw new IllegalArgumentException("L'effectif initial doit etre strictement positif.");
        }
    }

    private void normaliserCause(MortaliteModels mortalite) {
        if (mortalite.getCause() != null && mortalite.getCause().trim().isEmpty()) {
            mortalite.setCause(null);
        }
    }
}
