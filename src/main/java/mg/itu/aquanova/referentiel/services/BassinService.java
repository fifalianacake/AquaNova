package mg.itu.aquanova.referentiel.services;

import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.models.TypeBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;
import mg.itu.aquanova.referentiel.repositories.TypeBassinRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BassinService {

    private final BassinsRepository bassinRepository;

    private final TypeBassinRepository typeBassinRepository;

    private final StatutBassinRepository statutBassinRepository;

    public BassinService(BassinsRepository bassinRepository,
            TypeBassinRepository typeBassinRepository, StatutBassinRepository statutBassinRepository) {
        this.bassinRepository = bassinRepository;
        this.typeBassinRepository = typeBassinRepository;
        this.statutBassinRepository = statutBassinRepository;
    }

    public List<Bassin> findAll() {
        return bassinRepository.findAll();
    }

    public List<Bassin> getAllBassins() {
        return bassinRepository.findAll();
    }

    public Bassin getBassinById(Long id) {
        return bassinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bassin introuvable avec l'ID : " + id));
    }

    public List<Bassin> listerBassinsLibres() {
        StatutBassin libre = statutBassinRepository.findByLibelle(LibelleStatutBassin.LIBRE)
                .orElseThrow(() -> new IllegalArgumentException("Veuillez bien verifier le statut du bassin"));
        return bassinRepository.findAllByStatutOrderByReferenceAsc(libre);
    }

    @Transactional
    public Bassin saveBassin(Bassin bassin) {
        if (bassin.getReference() == null || bassin.getReference().trim().isEmpty()) {
            throw new IllegalArgumentException("La référence du bassin est obligatoire.");
        }

        if (bassin.getCapaciteM3() == null || bassin.getCapaciteM3().signum() <= 0) {
            throw new IllegalArgumentException(
                    "La capacité en m³ doit être strictement supérieure à 0.");
        }

        if (bassin.getTypeBassin() == null || bassin.getTypeBassin().getId() == null) {
            throw new IllegalArgumentException("Le type de bassin est obligatoire.");
        }

        if (bassin.getStatut() == null || bassin.getStatut().getId() == null) {
            throw new IllegalArgumentException("Le statut du bassin est obligatoire.");
        }

        bassinRepository.findByReference(bassin.getReference())
                .filter(autre -> !autre.getId().equals(bassin.getId()))
                .ifPresent(autre -> {
                    throw new IllegalStateException(
                            "Erreur : Le bassin '" + bassin.getReference() + "' existe déjà.");
                });

        TypeBassin typeBassin = typeBassinRepository.findById(bassin.getTypeBassin().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le type de bassin spécifié n'existe pas."));
        StatutBassin statut = statutBassinRepository.findById(bassin.getStatut().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le statut spécifié n'existe pas."));

        bassin.setTypeBassin(typeBassin);
        bassin.setStatut(statut);

        return bassinRepository.save(bassin);
    }

    public void deleteBassin(Long id) {
        bassinRepository.deleteById(id);
    }

    // ==========================
    // Gestion des Types
    // ==========================

    public List<TypeBassin> getAllTypes() {
        return typeBassinRepository.findAll();
    }

    public TypeBassin getTypeBassinById(Long id) {
        return typeBassinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Type de bassin introuvable : " + id));
    }

    public void saveTypeBassin(TypeBassin typeBassin) {
        typeBassinRepository.save(typeBassin);
    }

    public void deleteTypeBassin(Long id) {
        typeBassinRepository.deleteById(id);
    }
}
