package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.alerte.models.SeuilAlerte;
import mg.itu.aquanova.alerte.repositories.SeuilAlerteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class SeuilAlerteService {

    private final SeuilAlerteRepository seuilAlerteRepository;

    public SeuilAlerteService(SeuilAlerteRepository seuilAlerteRepository) {
        this.seuilAlerteRepository = seuilAlerteRepository;
    }

    @Transactional
    public SeuilAlerte create(SeuilAlerte seuil) {
        // Validation manuelle de doublon de code au niveau métier
        if (seuilAlerteRepository.findByCode(seuil.getCode()).isPresent()) {
            throw new IllegalArgumentException("Un seuil avec ce code existe déjà.");
        }
        return seuilAlerteRepository.save(seuil);
    }

    @Transactional
    public SeuilAlerte update(Long id, SeuilAlerte updatedSeuil) {
        SeuilAlerte existing = seuilAlerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seuil introuvable"));
        
        // Mise à jour via les setters d'origine
        existing.setLibelle(updatedSeuil.getLibelle());
        existing.setModuleSource(updatedSeuil.getModuleSource());
        existing.setValeur(updatedSeuil.getValeur());
        existing.setUnite(updatedSeuil.getUnite());
        existing.setDescription(updatedSeuil.getDescription());
        existing.setActif(updatedSeuil.getActif());
        
        return seuilAlerteRepository.save(existing);
    }

    @Transactional
    public void deleteOrDisable(Long id) {
        SeuilAlerte seuil = seuilAlerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seuil introuvable"));
        
        // Logique métier : On désactive au lieu de supprimer pour garder l'intégrité de l'historique
        seuil.setActif(false);
        seuilAlerteRepository.save(seuil);
    }

    public Page<SeuilAlerte> search(String moduleSource, String code, Boolean actif, Pageable pageable) {
        return seuilAlerteRepository.filtrerSeuils(moduleSource, code, actif, pageable);
    }

    public Optional<SeuilAlerte> getByCode(String code) {
        return seuilAlerteRepository.findByCode(code);
    }

    // Logique métier critique : Un seuil inactif ne doit pas déclencher d'alerte
    public Optional<SeuilAlerte> getSeuilActif(String code) {
        Optional<SeuilAlerte> seuilOpt = seuilAlerteRepository.findByCode(code);
        if (seuilOpt.isPresent() && seuilOpt.get().getActif()) {
            return seuilOpt;
        }
        return Optional.empty();
    }
}