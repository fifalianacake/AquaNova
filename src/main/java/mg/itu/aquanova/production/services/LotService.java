package mg.itu.aquanova.production.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        return repository.findAll(specification(filter), pageable);
    }

    private Specification<LotModels> specification(LotFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getCode() != null && !filter.getCode().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("code")), "%" + filter.getCode().trim().toLowerCase() + "%"));
            }
            if (filter.getEspeceId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("espece").get("id"), filter.getEspeceId()));
            }
            if (filter.getBassinId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bassin").get("id"), filter.getBassinId()));
            }
            if (filter.getStadeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("stadeCroissance").get("id"), filter.getStadeId()));
            }
            if (filter.getStatutId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("statutLot").get("id"), filter.getStatutId()));
            } else {
                predicates = cb.and(predicates, cb.or(
                        cb.isNull(root.get("statutLot")),
                        cb.notEqual(root.get("statutLot").get("libelle"), StatutLotEnum.ANNULE)));
            }
            if (filter.getDateFrom() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateMiseEnCharge"), filter.getDateFrom()));
            }
            if (filter.getDateTo() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateMiseEnCharge"), filter.getDateTo()));
            }
            if (filter.getEffectifMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("effectifActuel"), filter.getEffectifMin()));
            }
            if (filter.getEffectifMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("effectifActuel"), filter.getEffectifMax()));
            }

            return predicates;
        };
    }

    public LotModels trouverParId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lot introuvable: " + id));
    }

    private static final DateTimeFormatter FORMAT_JOUR_CODE = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    public String genererCodeLot(LocalDate date) {
        LocalDate jour = date != null ? date : LocalDate.now();
        String prefixe = "LOT-" + jour.format(FORMAT_JOUR_CODE) + "-";
        int prochaineSequence = repository.findFirstByCodeStartingWithOrderByCodeDesc(prefixe)
                .map(lot -> extraireSequence(lot.getCode()) + 1)
                .orElse(1);
        return prefixe + String.format("%03d", prochaineSequence);
    }

    private int extraireSequence(String code) {
        if (code == null) {
            return 0;
        }
        int dernierTiret = code.lastIndexOf('-');
        try {
            return Integer.parseInt(code.substring(dernierTiret + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Le code de lot est l'identifiant métier du lot (il sert notamment de clé de regroupement
     * dans les statistiques de vente) : il doit rester unique. {@code idLotIgnore} permet de
     * ne pas se comparer à soi-même lors d'une modification.
     */
    public void verifierCodeDisponible(String code, Long idLotIgnore) {
        boolean dejaPris = idLotIgnore == null
                ? repository.existsByCode(code)
                : repository.existsByCodeAndIdNot(code, idLotIgnore);
        if (dejaPris) {
            throw new IllegalArgumentException("Le code de lot « " + code + " » est déjà utilisé.");
        }
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
        boolean etaitActif = estActif(exist.getStatutLot());
        Bassin ancienBassin = exist.getBassin();
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

        if (etaitActif && !estActif(exist.getStatutLot())) {
            marquerBassinLibre(ancienBassin);
        }
        return repository.save(exist);
    }

    public void supprimer(Long id) {
        LotModels lot = trouverParId(id);
        StatutLotModels statutAnnule = statutLotRepository.findByLibelle(StatutLotEnum.ANNULE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de lot ANNULE introuvable."));

        boolean etaitActif = estActif(lot.getStatutLot());
        lot.setStatutLot(statutAnnule);

        if (etaitActif) {
            marquerBassinLibre(lot.getBassin());
        }

        repository.save(lot);
    }

    public void validerLot(LotModels lot, Long idLotIgnore) {
        if (lot == null) {
            throw new IllegalArgumentException("Le lot est obligatoire.");
        }
        if (lot.getCode() == null || lot.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code du lot est obligatoire.");
        }
        lot.setCode(lot.getCode().trim());
        verifierCodeDisponible(lot.getCode(), idLotIgnore);

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
        return statut != null
                && statut.getLibelle() != StatutLotEnum.CLOTURE
                && statut.getLibelle() != StatutLotEnum.ANNULE;
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

    private void marquerBassinLibre(Bassin bassinLot) {
        if (bassinLot == null || bassinLot.getId() == null) {
            return;
        }

        Bassin bassin = bassinsRepository.findById(bassinLot.getId())
                .orElseThrow(() -> new EntityNotFoundException("Bassin introuvable: " + bassinLot.getId()));
        StatutBassin statutLibre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                .orElseThrow(() -> new EntityNotFoundException("Statut de bassin LIBRE introuvable."));

        bassin.setStatut(statutLibre);
        bassinsRepository.save(bassin);
    }

    private void verifierBassinDisponible(Long bassinId, Long idLotIgnore) {
        List<LotModels> lotsActifsDuBassin = repository.findByBassinIdAndStatutLotLibelleNotIn(
                bassinId,
                List.of(StatutLotEnum.CLOTURE, StatutLotEnum.ANNULE));

        boolean occupeParUnAutreLot = lotsActifsDuBassin.stream()
                .anyMatch(lot -> idLotIgnore == null || !idLotIgnore.equals(lot.getId()));

        if (occupeParUnAutreLot) {
            throw new IllegalStateException("Ce bassin contient déjà un autre lot actif.");
        }
    }
}
