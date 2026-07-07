package mg.itu.aquanova.admin.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.admin.models.ParametreSysteme;
import mg.itu.aquanova.admin.repositories.ParametreSystemeRepository;

@Service
public class ParametreSystemeService {
    public static final String ICA_SYSTEME = "ICA_SYSTEME";
    public static final String STOCK_ALIMENT_MINIMUM_KG = "STOCK_ALIMENT_MINIMUM_KG";
    public static final String PERIODE_ANALYSE_CONSO_JOURS = "PERIODE_ANALYSE_CONSO_JOURS";
    public static final String HORIZON_PREVISION_STOCK_JOURS = "HORIZON_PREVISION_STOCK_JOURS";
    public static final String TEMP_EAU_MIN = "TEMP_EAU_MIN";
    public static final String TEMP_EAU_MAX = "TEMP_EAU_MAX";
    public static final String PH_MIN = "PH_MIN";
    public static final String PH_MAX = "PH_MAX";
    public static final String OXYGENE_MIN_MG_L = "OXYGENE_MIN_MG_L";
    public static final String SEUIL_PROCHE_RECOLTE_RATIO = "SEUIL_PROCHE_RECOLTE_RATIO";
    public static final String NB_MIN_PESEES_PREVISION_RECOLTE = "NB_MIN_PESEES_PREVISION_RECOLTE";

    private final ParametreSystemeRepository repository;

    public ParametreSystemeService(ParametreSystemeRepository repository) {
        this.repository = repository;
    }

    public List<ParametreSysteme> findAll() {
        return repository.findAll();
    }

    public ParametreSysteme findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ParametreSysteme findByCode(String code) {
        return repository.findByCode(code).orElse(null);
    }

    public String getValeur(String code, String valeurParDefaut) {
        ParametreSysteme parametre = findByCode(code);
        if (parametre == null || parametre.getValeur() == null || parametre.getValeur().isBlank()) {
            return valeurParDefaut;
        }
        return parametre.getValeur().trim();
    }

    public BigDecimal getDecimal(String code, BigDecimal valeurParDefaut) {
        String valeur = getValeur(code, null);
        if (valeur == null) {
            return valeurParDefaut;
        }

        try {
            return new BigDecimal(normaliserDecimal(valeur));
        } catch (NumberFormatException e) {
            return valeurParDefaut;
        }
    }

    public Double getDouble(String code, Double valeurParDefaut) {
        BigDecimal valeur = getDecimal(code, valeurParDefaut != null ? BigDecimal.valueOf(valeurParDefaut) : null);
        return valeur != null ? valeur.doubleValue() : null;
    }

    public Integer getInteger(String code, Integer valeurParDefaut) {
        String valeur = getValeur(code, null);
        if (valeur == null) {
            return valeurParDefaut;
        }

        try {
            return Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            return valeurParDefaut;
        }
    }

    public Boolean getBoolean(String code, Boolean valeurParDefaut) {
        String valeur = getValeur(code, null);
        if (valeur == null) {
            return valeurParDefaut;
        }
        return Boolean.parseBoolean(valeur);
    }

    public ParametreSysteme create(ParametreSysteme parametre) {
        if (repository.existsByCode(parametre.getCode())) {
            throw new RuntimeException("Ce code de paramètre existe déjà");
        }

        return repository.save(parametre);
    }

    public ParametreSysteme update(Long id, ParametreSysteme data) {
        ParametreSysteme parametre = findById(id);

        parametre.setCode(data.getCode());
        parametre.setLibelle(data.getLibelle());
        parametre.setValeur(data.getValeur());
        parametre.setTypeValeur(data.getTypeValeur());
        parametre.setDescription(data.getDescription());

        return repository.save(parametre);
    }

    public void delete(Long id) {
        ParametreSysteme parametre = findById(id);

        if (parametre == null) {
            throw new RuntimeException("Ce code n'existe pas");
        }

        repository.delete(parametre);
    }

    public void delete(String code) {
        ParametreSysteme parametre = findByCode(code);

        if (parametre == null) {
            throw new RuntimeException("Ce paramètre n'existe pas");
        }

        repository.delete(parametre);
    }

    private String normaliserDecimal(String valeur) {
        return valeur.trim().replace(",", ".");
    }
}
