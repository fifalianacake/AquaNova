package mg.itu.aquanova.production.services;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.models.TypeRecolteEnum;
import mg.itu.aquanova.production.models.TypeRecoltes;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.RecoltesRepository;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.production.repositories.TypeRecoltesRepository;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RecolteService {

    private final RecoltesRepository recoltesRepository;
    private final LotRepository lotRepository;
    private final TypeRecoltesRepository typeRecoltesRepository;
    private final StatutLotRepository statutLotRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final JournalLotService journalLotService;

    public RecolteService(
            RecoltesRepository recoltesRepository,
            LotRepository lotRepository,
            TypeRecoltesRepository typeRecoltesRepository,
            StatutLotRepository statutLotRepository,
            StatutBassinRepository statutBassinRepository,
            JournalLotService journalLotService) {
        this.recoltesRepository = recoltesRepository;
        this.lotRepository = lotRepository;
        this.typeRecoltesRepository = typeRecoltesRepository;
        this.statutLotRepository = statutLotRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.journalLotService = journalLotService;
    }

    @Transactional
    public Recoltes creerRecolte(
            Long lotId,
            Long typeRecolteId,
            LocalDate dateRecolte,
            Integer effectifRecolte,
            Double poidsTotal) {

        if (lotId == null) {
            throw new IllegalArgumentException("Le lot est obligatoire.");
        }
        if (typeRecolteId == null) {
            throw new IllegalArgumentException("Le type de récolte est obligatoire.");
        }

        LotModels lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable avec l'id : " + lotId));
        TypeRecoltes typeRecolte = typeRecoltesRepository.findById(typeRecolteId)
                .orElseThrow(() -> new EntityNotFoundException("Type de récolte introuvable avec l'id : " + typeRecolteId));

        validerRecolte(lot, typeRecolte, dateRecolte, effectifRecolte, poidsTotal);

        double poidsMoyen = poidsTotal / effectifRecolte;
        int nouvelEffectif = lot.getEffectifActuel() - effectifRecolte;

        Recoltes recolte = new Recoltes();
        recolte.setLot(lot);
        recolte.setTypeRecolte(typeRecolte);
        recolte.setDateRecolte(dateRecolte);
        recolte.setEffectifRecolte(effectifRecolte);
        recolte.setPoidsTotal(poidsTotal);
        recolte.setPoidsMoyen(poidsMoyen);

        lot.setEffectifActuel(nouvelEffectif);
        lot.setPoidsMoyenActuel(poidsMoyen);
        appliquerStatutsApresRecolte(lot, nouvelEffectif);

        Recoltes saved = recoltesRepository.save(recolte);
        lotRepository.save(lot);

        journalLotService.inscrireEvenement(
                lot,
                TypeEvenementLot.LibelleEvenement.RECOLTE,
                "Récolte " + typeRecolte.getLibelle()
                        + " de " + effectifRecolte
                        + " individus, poids total " + poidsTotal
                        + ", poids moyen " + poidsMoyen,
                dateRecolte);

        return saved;
    }

    public List<Recoltes> getAllRecoltes() {
        return recoltesRepository.findAll();
    }

    public Recoltes getRecolteById(Long id) {
        return recoltesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Récolte introuvable avec l'id : " + id));
    }

    public List<Recoltes> rechercherRecoltes(
            Long lotId,
            Long typeRecolteId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        Stream<Recoltes> stream = recoltesRepository.findAll().stream();

        if (lotId != null) {
            stream = stream.filter(r -> r.getLot() != null && lotId.equals(r.getLot().getId()));
        }
        if (typeRecolteId != null) {
            stream = stream.filter(r -> r.getTypeRecolte() != null && typeRecolteId.equals(r.getTypeRecolte().getId()));
        }
        if (dateFrom != null) {
            stream = stream.filter(r -> r.getDateRecolte() != null && !r.getDateRecolte().isBefore(dateFrom));
        }
        if (dateTo != null) {
            stream = stream.filter(r -> r.getDateRecolte() != null && !r.getDateRecolte().isAfter(dateTo));
        }

        return stream.toList();
    }

    private void validerRecolte(
            LotModels lot,
            TypeRecoltes typeRecolte,
            LocalDate dateRecolte,
            Integer effectifRecolte,
            Double poidsTotal) {

        if (dateRecolte == null) {
            throw new IllegalArgumentException("La date de récolte est obligatoire.");
        }
        if (effectifRecolte == null || effectifRecolte <= 0) {
            throw new IllegalArgumentException("L'effectif récolté doit être strictement positif.");
        }
        if (poidsTotal == null || poidsTotal <= 0) {
            throw new IllegalArgumentException("Le poids total doit être strictement positif.");
        }
        if (lot.getStatutLot() != null && lot.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE) {
            throw new IllegalStateException("Impossible d'enregistrer une récolte sur un lot déjà clôturé.");
        }
        if (lot.getEffectifActuel() == null || lot.getEffectifActuel() <= 0) {
            throw new IllegalStateException("Ce lot ne contient plus d'individus récoltables.");
        }
        if (effectifRecolte > lot.getEffectifActuel()) {
            throw new IllegalArgumentException("L'effectif récolté dépasse l'effectif actuel du lot.");
        }
        if (lot.getDateMiseEnCharge() != null && dateRecolte.isBefore(lot.getDateMiseEnCharge())) {
            throw new IllegalArgumentException("La date de récolte ne peut pas être avant la mise en charge du lot.");
        }
        if (typeRecolte.getLibelle() == TypeRecolteEnum.TOTALE
                && !effectifRecolte.equals(lot.getEffectifActuel())) {
            throw new IllegalArgumentException("Une récolte totale doit porter sur tout l'effectif actuel du lot.");
        }
    }

    private void appliquerStatutsApresRecolte(LotModels lot, int nouvelEffectif) {
        if (nouvelEffectif == 0) {
            StatutLotModels statutCloture = statutLotRepository.findByLibelle(StatutLotEnum.CLOTURE)
                    .orElseThrow(() -> new EntityNotFoundException("Statut de lot CLOTURE introuvable."));
            lot.setStatutLot(statutCloture);

            if (lot.getBassin() != null) {
                StatutBassin statutLibre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                        .orElseThrow(() -> new EntityNotFoundException("Statut de bassin LIBRE introuvable."));
                lot.getBassin().setStatut(statutLibre);
            }
            return;
        }

        StatutLotModels statutPartiel = statutLotRepository.findByLibelle(StatutLotEnum.RECOLTE_PARTIELLE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de lot RECOLTE_PARTIELLE introuvable."));
        lot.setStatutLot(statutPartiel);
    }
}
