package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.production.models.Recoltes; // Modifié ici
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class VenteService {

    private final VenteRepository repository;
    private final StatutVenteRepository statutRepository;

    public VenteService(VenteRepository repository, StatutVenteRepository statutRepository) {
        this.repository = repository;
        this.statutRepository = statutRepository;
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
        if (vente.getClient() == null || vente.getClient().trim().isEmpty()) throw new RuntimeException("Client obligatoire");
        if (vente.getRecolte() == null) throw new RuntimeException("Récolte obligatoire");
        if (vente.getDateVente() == null) throw new RuntimeException("Date obligatoire");
        if (vente.getPoidsVendu() == null || vente.getPoidsVendu() <= 0) throw new RuntimeException("Poids vendu doit être > 0");
        if (vente.getPrixUnitaire() == null || vente.getPrixUnitaire() <= 0) throw new RuntimeException("Prix unitaire doit être > 0");

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
        System.out.println("JOURNAL_VENTE: Création de la vente #" + sauvee.getId());
        return sauvee;
    }

    @Transactional
    public Vente update(Vente vente) {
        Vente ancienne = repository.findById(vente.getId()).orElseThrow();
        if (ancienne.getStatutVente().getCode() == StatutVenteEnum.VALIDEE || ancienne.getStatutVente().getCode() == StatutVenteEnum.PAYEE) {
            throw new RuntimeException("Impossible de modifier une vente validée.");
        }

        Double dispoPoids = calculerPoidsDisponibleRecolte(vente.getRecolte(), vente.getId());
        if (vente.getPoidsVendu() > dispoPoids) throw new RuntimeException("Poids indisponible.");

        return repository.save(vente);
    }

    @Transactional
    public void validerVente(Long id) {
        Vente v = repository.findById(id).orElseThrow();
        v.setStatutVente(statutRepository.findByCode(StatutVenteEnum.VALIDEE));
        repository.save(v);
    }

    @Transactional
    public void annulerVente(Long id) {
        Vente v = repository.findById(id).orElseThrow();
        v.setStatutVente(statutRepository.findByCode(StatutVenteEnum.ANNULEE));
        repository.save(v);
    }

    public List<Vente> search(Long id, String client, Long recolteId, Long lotId, LocalDate debut, LocalDate fin, Long statutId) {
        return repository.filtrerVentes(id, client, recolteId, lotId, debut, fin, statutId);
    }

    public Vente trouverParId(Long id) { return repository.findById(id).orElseThrow(); }
}