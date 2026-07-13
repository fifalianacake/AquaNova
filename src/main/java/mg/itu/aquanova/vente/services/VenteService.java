package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.alerte.services.AnalyseVerificationService;
import mg.itu.aquanova.production.models.Recoltes; // Modifié ici
import mg.itu.aquanova.production.services.RecolteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class VenteService {

    private final VenteRepository repository;
    private final StatutVenteRepository statutRepository;
    private final RecolteService recolteService;
    private final AnalyseVerificationService analyseVerificationService;

    public VenteService(VenteRepository repository, StatutVenteRepository statutRepository,
            RecolteService recolteService, AnalyseVerificationService analyseVerificationService) {
        this.repository = repository;
        this.statutRepository = statutRepository;
        this.recolteService = recolteService;
        this.analyseVerificationService = analyseVerificationService;
    }

    private void rafraichirStatutRecolte(Long recolteId) {
        Recoltes recolteAJour = recolteService.getRecolteById(recolteId);
        Double dispoApres = calculerPoidsDisponibleRecolte(recolteAJour, null);
        recolteService.mettreAJourStatutDisponibilite(recolteId, dispoApres);
    }

    public Double calculerPoidsDisponibleRecolte(Recoltes recolte, Long exceptionVenteId) {
        List<Vente> ventes = repository.findActiveVentesByRecolte(recolte.getId());
        double totalVendu = 0.0;
        for (Vente v : ventes) {
            if (exceptionVenteId == null || !v.getId().equals(exceptionVenteId)) {
                totalVendu += v.getPoidsVendu();
            }
        }
        return recolte.getPoidsTotal() - totalVendu; // Utilise le poidsTotal de Tommy
    }

    public Integer calculerEffectifDisponibleRecolte(Recoltes recolte, Long exceptionVenteId) {
        List<Vente> ventes = repository.findActiveVentesByRecolte(recolte.getId());
        int totalVendu = 0;
        for (Vente v : ventes) {
            if (v.getEffectifVendu() != null && (exceptionVenteId == null || !v.getId().equals(exceptionVenteId))) {
                totalVendu += v.getEffectifVendu();
            }
        }
        return recolte.getEffectifRecolte() - totalVendu; // Utilise l'effectifRecolte de Tommy
    }

    @Transactional
    public Vente create(Vente vente) {
        if (vente.getClient() == null || vente.getClient().getId() == null) {
            throw new RuntimeException("Client obligatoire");
        }
        if (!Boolean.TRUE.equals(vente.getClient().getActif())) {
            throw new RuntimeException("Ce client est désactivé : impossible de lui associer une nouvelle vente.");
        }
        if (vente.getRecolte() == null)
            throw new RuntimeException("Récolte obligatoire");
        if (vente.getDateVente() == null)
            throw new RuntimeException("Date obligatoire");
        if (vente.getPrixUnitaire() == null || vente.getPrixUnitaire() <= 0)
            throw new RuntimeException("Prix unitaire doit être > 0");

        resoudrePoidsEtEffectif(vente);

        Double dispoPoids = calculerPoidsDisponibleRecolte(vente.getRecolte(), null);
        if (vente.getPoidsVendu() > dispoPoids) {
            throw new RuntimeException("Poids indisponible. Reste seulement: " + dispoPoids + " kg");
        }

        if (vente.getEffectifVendu() != null) {
            Integer dispoEffectif = calculerEffectifDisponibleRecolte(vente.getRecolte(), null);
            if (vente.getEffectifVendu() > dispoEffectif) {
                throw new RuntimeException("Effectif insuffisant. Reste seulement: " + dispoEffectif + " pièces");
            }
        }

        StatutVente statutCree = statutRepository.findByCode(StatutVenteEnum.CREEE);
        vente.setStatutVente(statutCree);

        Vente sauvee = repository.save(vente);
        rafraichirStatutRecolte(sauvee.getRecolte().getId());
        return sauvee;
    }

    private void resoudrePoidsEtEffectif(Vente vente) {
        boolean poidsRenseigne = vente.getPoidsVendu() != null;
        boolean effectifRenseigne = vente.getEffectifVendu() != null;

        if (poidsRenseigne && effectifRenseigne) {
            throw new RuntimeException(
                    "Choisissez soit le poids vendu, soit l'effectif vendu : les deux ne peuvent pas être renseignés en même temps.");
        }
        if (!poidsRenseigne && !effectifRenseigne) {
            throw new RuntimeException("Le poids vendu ou l'effectif vendu est obligatoire.");
        }
        if (effectifRenseigne && vente.getEffectifVendu() <= 0) {
            throw new RuntimeException("Effectif vendu doit être > 0");
        }
        if (poidsRenseigne && vente.getPoidsVendu() <= 0) {
            throw new RuntimeException("Poids vendu doit être > 0");
        }

        Double poidsMoyen = vente.getRecolte() != null ? vente.getRecolte().getPoidsMoyen() : null;
        if (poidsMoyen == null || poidsMoyen <= 0) {
            if (effectifRenseigne) {
                throw new RuntimeException(
                        "Le poids moyen individuel de la récolte est inconnu : impossible de déduire le poids à partir de l'effectif. Renseignez directement le poids vendu.");
            }
            return;
        }

        if (effectifRenseigne) {
            vente.setPoidsVendu(arrondir2(vente.getEffectifVendu() * poidsMoyen));
        } else {
            vente.setEffectifVendu((int) Math.round(vente.getPoidsVendu() / poidsMoyen));
        }
    }

    private Double arrondir2(Double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }

    @Transactional
    public Vente update(Vente vente) {
        Vente ancienne = repository.findById(vente.getId()).orElseThrow();
        if (ancienne.getStatutVente().getCode() != StatutVenteEnum.CREEE) {
            throw new IllegalStateException(
                    "Seule une vente au statut CREEE peut être modifiée (statut actuel : "
                            + ancienne.getStatutVente().getCode() + ").");
        }

        // Empêche toute modification de la récolte / du statut via champs cachés
        vente.setRecolte(ancienne.getRecolte());
        vente.setStatutVente(ancienne.getStatutVente());

        if (vente.getClient() == null || vente.getClient().getId() == null)
            throw new RuntimeException("Client obligatoire");

        boolean clientInchange = ancienne.getClient() != null
                && ancienne.getClient().getId().equals(vente.getClient().getId());
        if (!clientInchange && !Boolean.TRUE.equals(vente.getClient().getActif())) {
            throw new RuntimeException("Ce client est désactivé : impossible de lui associer cette vente.");
        }

        if (vente.getDateVente() == null)
            throw new RuntimeException("Date obligatoire");
        if (vente.getPrixUnitaire() == null || vente.getPrixUnitaire() <= 0)
            throw new RuntimeException("Prix unitaire doit être > 0");

        resoudrePoidsEtEffectif(vente);

        Double dispoPoids = calculerPoidsDisponibleRecolte(vente.getRecolte(), vente.getId());
        if (vente.getPoidsVendu() > dispoPoids) {
            throw new RuntimeException("Poids indisponible. Reste seulement: " + dispoPoids + " kg");
        }

        if (vente.getEffectifVendu() != null) {
            Integer dispoEffectif = calculerEffectifDisponibleRecolte(vente.getRecolte(), vente.getId());
            if (vente.getEffectifVendu() > dispoEffectif) {
                throw new RuntimeException("Effectif insuffisant. Reste seulement: " + dispoEffectif + " pièces");
            }
        }

        Vente sauvegardee = repository.save(vente);
        rafraichirStatutRecolte(sauvegardee.getRecolte().getId());
        return sauvegardee;
    }

    @Transactional
    public void validerVente(Long id) {
        Vente v = repository.findById(id).orElseThrow();
        if (v.getStatutVente().getCode() != StatutVenteEnum.CREEE) {
            throw new IllegalStateException(
                    "Seule une vente au statut CREEE peut être validée (statut actuel : "
                            + v.getStatutVente().getCode() + ").");
        }
        v.setStatutVente(statutRepository.findByCode(StatutVenteEnum.VALIDEE));
        Vente sauvegardee = repository.save(v);
        reevaluerRentabiliteDuLot(sauvegardee);
    }

    @Transactional
    public void marquerPayee(Long id) {
        Vente v = repository.findById(id).orElseThrow();
        if (v.getStatutVente().getCode() != StatutVenteEnum.VALIDEE) {
            throw new IllegalStateException(
                    "Seule une vente validée peut être marquée comme payée (statut actuel : "
                            + v.getStatutVente().getCode() + ").");
        }
        v.setStatutVente(statutRepository.findByCode(StatutVenteEnum.PAYEE));
        Vente sauvegardee = repository.save(v);
        reevaluerRentabiliteDuLot(sauvegardee);
    }

    private void reevaluerRentabiliteDuLot(Vente vente) {
        if (vente == null || vente.getRecolte() == null || vente.getRecolte().getLot() == null) {
            return;
        }
        analyseVerificationService.verifierRentabiliteLot(vente.getRecolte().getLot());
    }

    @Transactional
    public void annulerVente(Long id) {
        Vente v = repository.findById(id).orElseThrow();
        StatutVenteEnum statutActuel = v.getStatutVente().getCode();
        if (statutActuel == StatutVenteEnum.ANNULEE) {
            throw new IllegalStateException("Cette vente est déjà annulée.");
        }
        if (statutActuel == StatutVenteEnum.PAYEE) {
            throw new IllegalStateException("Impossible d'annuler une vente déjà payée.");
        }
        v.setStatutVente(statutRepository.findByCode(StatutVenteEnum.ANNULEE));
        Vente sauvegardee = repository.save(v);
        rafraichirStatutRecolte(sauvegardee.getRecolte().getId());
        reevaluerRentabiliteDuLot(sauvegardee);
    }

    public Page<Vente> lister(TransactionFilterDTO filter, Pageable pageable) {
        return repository.findAll(specification(filter), pageable);
    }

    public List<Vente> listerPourExport(TransactionFilterDTO filter) {
        return repository.findAll(specification(filter), org.springframework.data.domain.Sort.by("dateVente").descending());
    }

    private Specification<Vente> specification(TransactionFilterDTO filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getClient() != null && !filter.getClient().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("client").get("nom")), likePattern(filter.getClient())));
            }
            if (filter.getIdRecolte() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("recolte").get("id"), filter.getIdRecolte()));
            }
            if (filter.getIdLot() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("recolte").get("lot").get("id"), filter.getIdLot()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateVente"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateVente"), filter.getDateFin()));
            }
            if (filter.getStatutId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("statutVente").get("id"), filter.getStatutId()));
            }
            if (filter.getMontantMin() != null) {
                predicates = cb.and(predicates, cb.ge(cb.prod(root.get("poidsVendu"), root.get("prixUnitaire")), filter.getMontantMin()));
            }
            if (filter.getMontantMax() != null) {
                predicates = cb.and(predicates, cb.le(cb.prod(root.get("poidsVendu"), root.get("prixUnitaire")), filter.getMontantMax()));
            }

            return predicates;
        };
    }

    public Vente trouverParId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Vente> getByClient(Long id) {
        return repository.findByClientId(id);
    }

    private String likePattern(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
