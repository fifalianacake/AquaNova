package mg.itu.aquanova.production.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.MortaliteRepository;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class MortaliteService {

    private final MortaliteRepository repo;
    private final LotRepository lotRepository;
    private final StatutLotRepository statutLotRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final JournalLotService journalLotService;

    public MortaliteService(
            MortaliteRepository repo,
            LotRepository lotRepository,
            StatutLotRepository statutLotRepository,
            StatutBassinRepository statutBassinRepository,
            JournalLotService journalLotService) {
        this.repo = repo;
        this.lotRepository = lotRepository;
        this.statutLotRepository = statutLotRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.journalLotService = journalLotService;
    }

    public List<MortaliteModels> findAll() {
        return repo.findAll();
    }

    public MortaliteModels findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public List<MortaliteModels> findByLot(Long lotId) {
        return repo.findByLotIdOrderByDateMortaliteDesc(lotId);
    }

    public List<MortaliteModels> findByLotAndPeriode(Long lotId, LocalDate dateDebut, LocalDate dateFin) {
        return repo.findByLotIdAndDateMortaliteBetweenOrderByDateMortaliteDesc(lotId, dateDebut, dateFin);
    }

    @Transactional
    public MortaliteModels save(MortaliteModels mortalite) {
        validerMortalite(mortalite);

        LotModels lot = trouverLot(mortalite.getLot().getId());
        verifierLotNonCloture(lot);
        verifierDateApresMiseEnCharge(lot, mortalite.getDateMortalite());

        MortaliteModels ancienneMortalite = null;
        int anciensMorts = 0;
        if (mortalite.getId() != null) {
            ancienneMortalite = repo.findById(mortalite.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Mortalité introuvable: " + mortalite.getId()));
            if (!ancienneMortalite.getLot().getId().equals(lot.getId())) {
                throw new IllegalArgumentException("Le lot d'une mortalité existante ne peut pas être modifié.");
            }
            anciensMorts = ancienneMortalite.getNbMorts();
        }

        int effectifDisponible = valeurPositive(lot.getEffectifActuel()) + anciensMorts;
        if (mortalite.getNbMorts() > effectifDisponible) {
            throw new IllegalArgumentException("Le nombre de morts ne peut pas dépasser l'effectif actuel du lot.");
        }

        int nouvelEffectif = effectifDisponible - mortalite.getNbMorts();
        normaliserCause(mortalite);

        MortaliteModels cible = ancienneMortalite != null ? ancienneMortalite : new MortaliteModels();
        cible.setLot(lot);
        cible.setDateMortalite(mortalite.getDateMortalite());
        cible.setNbMorts(mortalite.getNbMorts());
        cible.setCause(mortalite.getCause());

        MortaliteModels saved = repo.save(cible);

        lot.setEffectifActuel(nouvelEffectif);
        if (nouvelEffectif == 0) {
            cloturerLotEtLibererBassin(lot);
        }
        lotRepository.save(lot);

        journalLotService.inscrireEvenement(
                lot,
                TypeEvenementLot.LibelleEvenement.MORTALITE,
                "Mortalité de " + mortalite.getNbMorts()
                        + " individus"
                        + (mortalite.getCause() != null ? ", cause: " + mortalite.getCause() : ""),
                mortalite.getDateMortalite());

        return saved;
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public Integer getTotalMortsByLot(Long lotId) {
        validerIdLot(lotId);
        return repo.sumNbMortsByLotId(lotId);
    }

    public Integer calculerEffectifVivant(Long lotId, Integer effectifInitial) {
        validerIdLot(lotId);
        validerEffectifInitial(effectifInitial);

        int effectifVivant = effectifInitial - getTotalMortsByLot(lotId);
        if (effectifVivant < 0) {
            throw new IllegalStateException("Le cumul des mortalités dépasse l'effectif initial du lot.");
        }
        return effectifVivant;
    }

    public Double calculerTauxSurvie(Integer effectifVivant, Integer effectifInitial) {
        validerEffectifInitial(effectifInitial);
        if (effectifVivant == null || effectifVivant < 0) {
            throw new IllegalArgumentException("L'effectif vivant doit être positif ou nul.");
        }
        if (effectifVivant > effectifInitial) {
            throw new IllegalArgumentException("L'effectif vivant ne peut pas dépasser l'effectif initial.");
        }

        return round2((effectifVivant * 100.0) / effectifInitial);
    }

    public Double calculerTauxSurvieByLot(Long lotId, Integer effectifInitial) {
        Integer effectifVivant = calculerEffectifVivant(lotId, effectifInitial);
        return calculerTauxSurvie(effectifVivant, effectifInitial);
    }

    public Double calculerBiomasseActive(Integer effectifVivant, Double poidsMoyen) {
        if (effectifVivant == null || effectifVivant < 0) {
            throw new IllegalArgumentException("L'effectif vivant doit être positif ou nul.");
        }
        if (poidsMoyen == null || poidsMoyen <= 0) {
            throw new IllegalArgumentException("Le poids moyen doit être strictement positif.");
        }

        return poidsMoyen * effectifVivant;
    }

    public Double calculerBiomasseActiveByLot(Long lotId, Integer effectifInitial, Double poidsMoyen) {
        Integer effectifVivant = calculerEffectifVivant(lotId, effectifInitial);
        return calculerBiomasseActive(effectifVivant, poidsMoyen);
    }

    private void validerMortalite(MortaliteModels mortalite) {
        if (mortalite == null) {
            throw new IllegalArgumentException("La mortalité est obligatoire.");
        }
        if (mortalite.getLot() == null || mortalite.getLot().getId() == null) {
            throw new IllegalArgumentException("La mortalité doit être associée à un lot.");
        }
        if (mortalite.getDateMortalite() == null) {
            throw new IllegalArgumentException("La date de mortalité est obligatoire.");
        }
        if (mortalite.getNbMorts() == null || mortalite.getNbMorts() <= 0) {
            throw new IllegalArgumentException("Le nombre de morts doit être strictement positif.");
        }
    }

    private void validerIdLot(Long lotId) {
        if (lotId == null) {
            throw new IllegalArgumentException("La mortalité doit être associée à un lot.");
        }
    }

    private void validerEffectifInitial(Integer effectifInitial) {
        if (effectifInitial == null || effectifInitial <= 0) {
            throw new IllegalArgumentException("L'effectif initial doit être strictement positif.");
        }
    }

    private LotModels trouverLot(Long lotId) {
        return lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + lotId));
    }

    private void verifierLotNonCloture(LotModels lot) {
        if (lot.getStatutLot() != null
                && (lot.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE
                        || lot.getStatutLot().getLibelle() == StatutLotEnum.ANNULE)) {
            throw new IllegalStateException("Impossible d'enregistrer une mortalité sur un lot clôturé ou annulé.");
        }
    }

    private void verifierDateApresMiseEnCharge(LotModels lot, LocalDate dateMortalite) {
        if (lot.getDateMiseEnCharge() != null && dateMortalite.isBefore(lot.getDateMiseEnCharge())) {
            throw new IllegalArgumentException("La date de mortalité ne peut pas être antérieure à la date de mise en charge du lot.");
        }
    }

    private void cloturerLotEtLibererBassin(LotModels lot) {
        StatutLotModels statutCloture = statutLotRepository.findByLibelle(StatutLotEnum.CLOTURE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de lot CLOTURE introuvable."));
        lot.setStatutLot(statutCloture);

        if (lot.getBassin() != null) {
            StatutBassin statutLibre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                    .orElseThrow(() -> new EntityNotFoundException("Statut de bassin LIBRE introuvable."));
            lot.getBassin().setStatut(statutLibre);
        }
    }

    private int valeurPositive(Integer valeur) {
        return valeur != null ? Math.max(valeur, 0) : 0;
    }

    private void normaliserCause(MortaliteModels mortalite) {
        if (mortalite.getCause() != null && mortalite.getCause().trim().isEmpty()) {
            mortalite.setCause(null);
        }
    }

    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
