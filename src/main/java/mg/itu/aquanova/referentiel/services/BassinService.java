package mg.itu.aquanova.referentiel.services;

import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.models.TypeBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;
import mg.itu.aquanova.referentiel.repositories.TypeBassinRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public void saveBassin(Bassin bassin) {
        bassinRepository.save(bassin);
    }

    public void deleteBassin(Long id) {
        bassinRepository.deleteById(id);
    }

    // ==========================
    // Création avec validation
    // ==========================

    @Transactional
    public Bassin creerBassin(String reference,
            Long idStatut,
            Long idType,
            Double capaciteM3) {

        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence du bassin est obligatoire.");
        }

        if (idStatut == null) {
            throw new IllegalArgumentException("Le statut du bassin est obligatoire.");
        }

        if (capaciteM3 == null || capaciteM3 <= 0) {
            throw new IllegalArgumentException(
                    "La capacité en m³ doit être strictement supérieure à 0.");
        }

        if (bassinRepository.findByReference(reference).isPresent()) {
            throw new IllegalStateException(
                    "Erreur : Le bassin '" + reference + "' existe déjà.");
        }

        TypeBassin typeBassin = typeBassinRepository.findById(idType)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le type de bassin spécifié n'existe pas."));
        StatutBassin statut = statutBassinRepository.findById(idStatut)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le statut spécifié n'existe pas."));

        Bassin bassin = new Bassin();
        bassin.setReference(reference);
        bassin.setStatut(statut);
        bassin.setCapaciteM3(BigDecimal.valueOf(capaciteM3));
        bassin.setTypeBassin(typeBassin);

        return bassinRepository.save(bassin);
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
