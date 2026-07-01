package mg.itu.aquanova.production.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.models.TransfertModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.production.repositories.TransfertRepository;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class TransfertService {

    private final TransfertRepository transfertRepository;
    private final LotRepository lotRepository;
    private final BassinsRepository bassinRepository;
    private final StatutLotRepository statutLotRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final JournalLotService journalLotService;

    public TransfertService(
            TransfertRepository transfertRepository,
            LotRepository lotRepository,
            BassinsRepository bassinRepository,
            StatutLotRepository statutLotRepository,
            StatutBassinRepository statutBassinRepository,
            JournalLotService journalLotService) {
        this.transfertRepository = transfertRepository;
        this.lotRepository = lotRepository;
        this.bassinRepository = bassinRepository;
        this.statutLotRepository = statutLotRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.journalLotService = journalLotService;
    }

    public List<TransfertModels> getAllTransferts() {
        return transfertRepository.findAll();
    }

    public TransfertModels getTransfertById(Long id) {
        return transfertRepository.findById(id).orElse(null);
    }

    @Transactional
    public TransfertModels saveTransfert(TransfertModels transfert) {
        validerTransfert(transfert);

        LotModels lotSource = trouverLot(transfert.getLotSource().getId());
        Bassin bassinSource = trouverBassin(transfert.getBassinSource().getId());
        Bassin bassinDestination = trouverBassin(transfert.getBassinDestination().getId());

        verifierLotSource(lotSource, bassinSource, transfert);
        verifierDateApresMiseEnCharge(lotSource, transfert);
        verifierBassinDestinationLibre(bassinDestination);

        int effectifInitialSource = lotSource.getEffectifActuel();
        boolean transfertTotal = transfert.getEffectif().equals(effectifInitialSource);
        LotModels lotDestination = transfertTotal
                ? appliquerTransfertTotal(lotSource, bassinDestination)
                : creerLotDestination(transfert, lotSource, bassinDestination);

        if (!transfertTotal) {
            lotSource.setEffectifActuel(effectifInitialSource - transfert.getEffectif());
        }

        if (transfertTotal) {
            marquerBassinLibre(bassinSource);
        }
        marquerBassinOccupe(bassinDestination);

        TransfertModels saved = new TransfertModels();
        saved.setLotSource(lotSource);
        saved.setLotDestination(lotDestination);
        saved.setBassinSource(bassinSource);
        saved.setBassinDestination(bassinDestination);
        saved.setDateTransfert(transfert.getDateTransfert());
        saved.setEffectif(transfert.getEffectif());
        saved.setPoidsMoyen(transfert.getPoidsMoyen());

        lotRepository.save(lotSource);
        lotRepository.save(lotDestination);
        bassinRepository.save(bassinSource);
        bassinRepository.save(bassinDestination);

        TransfertModels transfertSauvegarde = transfertRepository.save(saved);

        journalLotService.inscrireEvenement(
                lotSource,
                TypeEvenementLot.LibelleEvenement.TRANSFERT,
                "Transfert " + (transfertTotal ? "total" : "partiel")
                        + " de " + transfert.getEffectif()
                        + " individus vers le bassin " + bassinDestination.getReference(),
                transfert.getDateTransfert());

        if (!transfertTotal) {
            journalLotService.inscrireEvenement(
                    lotDestination,
                    TypeEvenementLot.LibelleEvenement.TRANSFERT,
                    "Création du lot par transfert partiel depuis " + lotSource.getCode()
                            + ", effectif " + transfert.getEffectif(),
                    transfert.getDateTransfert());
        }

        return transfertSauvegarde;
    }

    public void deleteTransfert(Long id) {
        transfertRepository.deleteById(id);
    }

    public TransfertModels updateTransfert(Long id, TransfertModels updatedTransfert) {
        throw new UnsupportedOperationException("La modification d'un transfert déjà appliqué n'est pas supportée.");
    }

    private void validerTransfert(TransfertModels transfert) {
        if (transfert == null) {
            throw new IllegalArgumentException("Le transfert est obligatoire.");
        }
        if (transfert.getLotSource() == null || transfert.getLotSource().getId() == null) {
            throw new IllegalArgumentException("Le lot source est obligatoire.");
        }
        if (transfert.getBassinSource() == null || transfert.getBassinSource().getId() == null) {
            throw new IllegalArgumentException("Le bassin source est obligatoire.");
        }
        if (transfert.getBassinDestination() == null || transfert.getBassinDestination().getId() == null) {
            throw new IllegalArgumentException("Le bassin destination est obligatoire.");
        }
        if (transfert.getDateTransfert() == null) {
            throw new IllegalArgumentException("La date de transfert est obligatoire.");
        }
        if (transfert.getEffectif() == null || transfert.getEffectif() <= 0) {
            throw new IllegalArgumentException("L'effectif transféré doit être strictement positif.");
        }
        if (transfert.getPoidsMoyen() == null || transfert.getPoidsMoyen().signum() <= 0) {
            throw new IllegalArgumentException("Le poids moyen doit être strictement positif.");
        }
    }

    private void verifierLotSource(LotModels lotSource, Bassin bassinSource, TransfertModels transfert) {
        if (lotSource.getStatutLot() != null && lotSource.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE) {
            throw new IllegalStateException("Impossible de transférer un lot clôturé.");
        }
        if (lotSource.getBassin() == null || !lotSource.getBassin().getId().equals(bassinSource.getId())) {
            throw new IllegalArgumentException("Le bassin source ne correspond pas au bassin actuel du lot source.");
        }
        if (transfert.getBassinDestination().getId().equals(bassinSource.getId())) {
            throw new IllegalArgumentException("Le transfert vers le même bassin est interdit.");
        }
        if (lotSource.getEffectifActuel() == null || lotSource.getEffectifActuel() <= 0) {
            throw new IllegalStateException("Le lot source ne contient plus d'individus transférables.");
        }
        if (transfert.getEffectif() > lotSource.getEffectifActuel()) {
            throw new IllegalArgumentException("L'effectif transféré dépasse l'effectif actuel du lot source.");
        }
        boolean transfertPartiel = transfert.getEffectif() < lotSource.getEffectifActuel();
        if (transfertPartiel && (transfert.getCodeLotDestination() == null || transfert.getCodeLotDestination().trim().isEmpty())) {
            throw new IllegalArgumentException("Le code du nouveau lot destination est obligatoire pour un transfert partiel.");
        }
    }

    private void verifierDateApresMiseEnCharge(LotModels lotSource, TransfertModels transfert) {
        if (lotSource.getDateMiseEnCharge() != null && transfert.getDateTransfert().isBefore(lotSource.getDateMiseEnCharge())) {
            throw new IllegalArgumentException("La date de transfert ne peut pas être antérieure à la date de mise en charge du lot source.");
        }
    }

    private void verifierBassinDestinationLibre(Bassin bassinDestination) {
        boolean destinationOccupee = lotRepository
                .findByBassinIdAndStatutLotLibelleNot(bassinDestination.getId(), StatutLotEnum.CLOTURE)
                .stream()
                .anyMatch(lot -> lot.getEffectifActuel() != null && lot.getEffectifActuel() > 0);

        if (destinationOccupee) {
            throw new IllegalStateException("Le bassin destination est déjà occupé par un autre lot actif.");
        }
    }

    private LotModels appliquerTransfertTotal(LotModels lotSource, Bassin bassinDestination) {
        lotSource.setBassin(bassinDestination);
        return lotSource;
    }

    private LotModels creerLotDestination(TransfertModels transfert, LotModels lotSource, Bassin bassinDestination) {
        LotModels lotDestination = new LotModels();
        lotDestination.setCode(transfert.getCodeLotDestination().trim());
        lotDestination.setEspece(lotSource.getEspece());
        lotDestination.setBassin(bassinDestination);
        lotDestination.setStadeCroissance(lotSource.getStadeCroissance());
        lotDestination.setStatutLot(statutActifParDefaut(lotSource));
        lotDestination.setDateMiseEnCharge(transfert.getDateTransfert());
        lotDestination.setEffectifInitial(transfert.getEffectif());
        lotDestination.setEffectifActuel(transfert.getEffectif());
        lotDestination.setPoidsMoyenInitial(transfert.getPoidsMoyen().doubleValue());
        lotDestination.setPoidsMoyenActuel(transfert.getPoidsMoyen().doubleValue());
        lotDestination.setObservation("Créé par transfert partiel depuis le lot " + lotSource.getCode());
        return lotRepository.save(lotDestination);
    }

    private StatutLotModels statutActifParDefaut(LotModels lotSource) {
        if (lotSource.getStatutLot() != null && lotSource.getStatutLot().getLibelle() != StatutLotEnum.CLOTURE) {
            return lotSource.getStatutLot();
        }
        return statutLotRepository.findByLibelle(StatutLotEnum.EN_CROISSANCE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de lot EN_CROISSANCE introuvable."));
    }

    private void marquerBassinLibre(Bassin bassin) {
        StatutBassin statutLibre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de bassin LIBRE introuvable."));
        bassin.setStatut(statutLibre);
    }

    private void marquerBassinOccupe(Bassin bassin) {
        StatutBassin statutOccupe = statutBassinRepository.findByLibelle(LibelleStatutBassin.OCCUPE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de bassin OCCUPE introuvable."));
        bassin.setStatut(statutOccupe);
    }

    private LotModels trouverLot(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + id));
    }

    private Bassin trouverBassin(Long id) {
        return bassinRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bassin introuvable: " + id));
    }
}
