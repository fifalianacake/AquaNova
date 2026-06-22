package mg.itu.aquanova.referentiel.services;

import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.TypeBassin;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.TypeBassinRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BassinService {

    @Autowired
    private BassinsRepository bassinRepository;

    @Autowired
    private TypeBassinRepository typeBassinRepository;

    // ==========================
    // Gestion des Bassins
    // ==========================

    public List<Bassin> getAllBassins() {
        return bassinRepository.findAll();
    }

    public Bassin getBassinById(Long id) {
        return bassinRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bassin introuvable avec l'ID : " + id));
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
                              Integer idStatut,
                              Long idType,
                              BigDecimal capaciteM3) {

        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence du bassin est obligatoire.");
        }

        if (idStatut == null) {
            throw new IllegalArgumentException("Le statut du bassin est obligatoire.");
        }

        if (capaciteM3 == null ||
                capaciteM3.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La capacité en m³ doit être strictement supérieure à 0.");
        }

        if (bassinRepository.findByReference(reference).isPresent()) {
            throw new IllegalStateException(
                    "Erreur : Le bassin '" + reference + "' existe déjà.");
        }

        TypeBassin typeBassin = typeBassinRepository.findById(idType)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Le type de bassin spécifié n'existe pas."));

        Bassin bassin = new Bassin();
        bassin.setReference(reference);
        bassin.setIdStatut(idStatut);
        bassin.setCapaciteM3(capaciteM3);
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
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Type de bassin introuvable : " + id));
    }

    public void saveTypeBassin(TypeBassin typeBassin) {
        typeBassinRepository.save(typeBassin);
    }

    public void deleteTypeBassin(Long id) {
        typeBassinRepository.deleteById(id);
    }
}
