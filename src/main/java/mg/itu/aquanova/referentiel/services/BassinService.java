package mg.itu.aquanova.referentiel.services;

import com.aquanova.model.Bassins;
import com.aquanova.model.TypeBassin;
import com.aquanova.repository.BassinsRepository;
import com.aquanova.repository.TypeBassinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BassinService {

    @Autowired
    private BassinsRepository bassinsRepository;

    @Autowired
    private TypeBassinRepository typeBassinRepository;

    public List<Bassins> listerTousLesBassins() {
        return bassinsRepository.findAll();
    }

    @Transactional
    public Bassins creerBassin(String reference, Integer idStatut, Long idType, BigDecimal capaciteM3) {
        
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence du bassin est obligatoire.");
        }
        if (idStatut == null) {
            throw new IllegalArgumentException("Le statut du bassin est obligatoire.");
        }
        if (capaciteM3 == null || capaciteM3.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La capacité en m³ doit être strictement supérieure à 0.");
        }
        if (bassinsRepository.findByReference(reference).isPresent()) {
            throw new IllegalStateException("Erreur : Le bassin '" + reference + "' existe déjà.");
        }
        TypeBassin typeBassin = typeBassinRepository.findById(idType)
                .orElseThrow(() -> new IllegalArgumentException("Le type de bassin spécifié n'existe pas."));

       
        Bassins nouveauBassin = new Bassins();
        nouveauBassin.setReference(reference);
        nouveauBassin.setIdStatut(idStatut);
        nouveauBassin.setCapaciteM3(capaciteM3);
        nouveauBassin.setTypeBassin(typeBassin); 

        return bassinsRepository.save(nouveauBassin);
    }
}
