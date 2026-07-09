package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.production.models.Recoltes; // Modifié ici
import mg.itu.aquanova.production.services.RecolteService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class VenteService {

    private final VenteRepository repository;
    private final StatutVenteRepository statutRepository;
    private final RecolteService recolteService;

    public VenteService(VenteRepository repository, StatutVenteRepository statutRepository,
            RecolteService recolteService) {
        this.repository = repository;
        this.statutRepository = statutRepository;
        this.recolteService = recolteService;
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

    /**
     * Résout le couple poids/effectif vendu à partir d'un seul des deux champs (saisie exclusive
     * imposée par le formulaire) : l'autre est déduit via le poids moyen individuel de la récolte.
     * Rejette explicitement le cas où les deux seraient renseignés en même temps.
     */
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
        repository.save(v);
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
        repository.save(v);
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
    }

    public List<Vente> search(TransactionFilterDTO filters) {
        if (filters == null) {
            filters = new TransactionFilterDTO();
        }

        return repository.searchTransactions(
                filters.getId(),
                likePattern(filters.getClient()),
                filters.getIdRecolte(),
                filters.getIdLot(),
                filters.getDateDebut(),
                filters.getDateFin(),
                filters.getStatutId(),
                filters.getMontantMin() != null ? filters.getMontantMin().doubleValue() : null,
                filters.getMontantMax() != null ? filters.getMontantMax().doubleValue() : null);
    }

    public List<Vente> search(Long id, String client, Long recolteId, Long lotId, LocalDate debut, LocalDate fin,
            Long statutId) {
        TransactionFilterDTO filters = new TransactionFilterDTO();
        filters.setId(id);
        filters.setClient(client);
        filters.setIdRecolte(recolteId);
        filters.setIdLot(lotId);
        filters.setDateDebut(debut);
        filters.setDateFin(fin);
        filters.setStatutId(statutId);
        return search(filters);
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
