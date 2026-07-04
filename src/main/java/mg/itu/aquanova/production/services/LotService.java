package mg.itu.aquanova.production.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;

@Service
public class LotService {

    private final LotRepository repository;
    private final StatutLotRepository statutLotRepository;
    private final BassinsRepository bassinsRepository;
    private final StatutBassinRepository statutBassinRepository;

    public LotService(
            LotRepository repository,
            StatutLotRepository statutLotRepository,
            BassinsRepository bassinsRepository,
            StatutBassinRepository statutBassinRepository) {
        this.repository = repository;
        this.statutLotRepository = statutLotRepository;
        this.bassinsRepository = bassinsRepository;
        this.statutBassinRepository = statutBassinRepository;
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
        validerLot(lot, null);
        initialiserValeursActuelles(lot);
        if (estActif(lot.getStatutLot())) {
            marquerBassinOccupe(lot);
        }
        return repository.save(lot);
    }

    public LotModels modifier(Long id, LotModels lot) {
        validerLot(lot, id);
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

    public void validerLot(LotModels lot, Long idLotIgnore) {
        if (lot == null) {
            throw new IllegalArgumentException("Le lot est obligatoire.");
        }
        if (lot.getCode() == null || lot.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du lot est obligatoire.");
        }
        if (lot.getEspece() == null || lot.getEspece().getId() == null) {
            throw new IllegalArgumentException("Le lot doit être associé à une espèce.");
        }
        if (lot.getBassin() == null || lot.getBassin().getId() == null) {
            throw new IllegalArgumentException("Le lot doit être associé à un bassin.");
        }
        if (lot.getStadeCroissance() == null || lot.getStadeCroissance().getId() == null) {
            throw new IllegalArgumentException("Le lot doit être associé à un stade de croissance.");
        }
        if (lot.getStatutLot() == null || lot.getStatutLot().getId() == null) {
            throw new IllegalArgumentException("Le lot doit être associé à un statut.");
        }

        StatutLotModels statut = statutLotRepository.findById(lot.getStatutLot().getId())
                .orElseThrow(() -> new EntityNotFoundException("Statut de lot introuvable: " + lot.getStatutLot().getId()));
        lot.setStatutLot(statut);

        if (estActif(statut)) {
            verifierBassinDisponible(lot.getBassin().getId(), idLotIgnore);
        }
    }

    private boolean estActif(StatutLotModels statut) {
        return statut != null && statut.getLibelle() != StatutLotEnum.CLOTURE;
    }

    private void initialiserValeursActuelles(LotModels lot) {
        lot.setEffectifActuel(lot.getEffectifInitial());
        lot.setPoidsMoyenActuel(lot.getPoidsMoyenInitial());
    }

    private void marquerBassinOccupe(LotModels lot) {
        Bassin bassin = bassinsRepository.findById(lot.getBassin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Bassin introuvable: " + lot.getBassin().getId()));
        StatutBassin statutOccupe = statutBassinRepository.findByLibelle(LibelleStatutBassin.OCCUPE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de bassin OCCUPE introuvable."));

        bassin.setStatut(statutOccupe);
        lot.setBassin(bassinsRepository.save(bassin));
    }

    private void verifierBassinDisponible(Long bassinId, Long idLotIgnore) {
        List<LotModels> lotsActifsDuBassin = repository.findByBassinIdAndStatutLotLibelleNot(
                bassinId,
                StatutLotEnum.CLOTURE);

        boolean occupeParUnAutreLot = lotsActifsDuBassin.stream()
                .anyMatch(lot -> idLotIgnore == null || !idLotIgnore.equals(lot.getId()));

        if (occupeParUnAutreLot) {
            throw new IllegalStateException("Ce bassin contient déjà un autre lot actif.");
        }
    }
}
