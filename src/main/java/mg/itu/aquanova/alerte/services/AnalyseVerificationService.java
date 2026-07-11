package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.admin.service.ParametreSystemeService;
import mg.itu.aquanova.alerte.dto.AlerteCreateDTO;
import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.sanitaire_equipement.models.ReleveEau;

import org.springframework.stereotype.Service;

@Service
public class AnalyseVerificationService {

    private final ParametreSystemeService parametreSystemeService;
    private final AlerteService alerteService;
    private final BassinsRepository bassinsRepository;
    private final AlimentRepository alimentRepository;

    public AnalyseVerificationService(ParametreSystemeService parametreSystemeService,
                                      AlerteService alerteService,
                                      BassinsRepository bassinsRepository,
                                      AlimentRepository alimentRepository) {
        this.parametreSystemeService = parametreSystemeService;
        this.alerteService = alerteService;
        this.bassinsRepository = bassinsRepository;
        this.alimentRepository = alimentRepository;
    }
    public void verifierQualiteEau(ReleveEau releve) {
        if (releve == null || releve.getBassin() == null) {
            return;
        }

        Double tempMin = parametreSystemeService.getDouble(ParametreSystemeService.TEMP_EAU_MIN, null);
        Double tempMax = parametreSystemeService.getDouble(ParametreSystemeService.TEMP_EAU_MAX, null);
        Double phMin = parametreSystemeService.getDouble(ParametreSystemeService.PH_MIN, null);
        Double phMax = parametreSystemeService.getDouble(ParametreSystemeService.PH_MAX, null);
        Double oxygeneMin = parametreSystemeService.getDouble(ParametreSystemeService.OXYGENE_MIN_MG_L, null);

        StringBuilder anomalies = new StringBuilder();

        Double temperature = releve.getTemperature();
        if (temperature != null) {
            if (tempMin != null && temperature < tempMin) {
                ajouter(anomalies, "température " + temperature + " °C sous le minimum (" + tempMin + " °C)");
            } else if (tempMax != null && temperature > tempMax) {
                ajouter(anomalies, "température " + temperature + " °C au-dessus du maximum (" + tempMax + " °C)");
            }
        }

        Double ph = releve.getPh();
        if (ph != null) {
            if (phMin != null && ph < phMin) {
                ajouter(anomalies, "pH " + ph + " sous le minimum (" + phMin + ")");
            } else if (phMax != null && ph > phMax) {
                ajouter(anomalies, "pH " + ph + " au-dessus du maximum (" + phMax + ")");
            }
        }

        Double oxygene = releve.getOxygene();
        if (oxygene != null && oxygeneMin != null && oxygene < oxygeneMin) {
            ajouter(anomalies, "oxygène dissous " + oxygene + " mg/L sous le minimum (" + oxygeneMin + " mg/L)");
        }

        if (anomalies.length() == 0) {
            return;
        }

        Bassin bassin = bassinsRepository.findById(releve.getBassin().getId()).orElse(null);
        if (bassin == null) {
            return;
        }

        String message = "Qualité d'eau non conforme dans le bassin " + bassin.getReference()
                + " : " + anomalies + ".";

        alerteService.creerSiNonExiste(AlerteCreateDTO.pourBassin(
                ModuleSource.SANITAIRE, TypeAlerte.QUALITE_EAU, NiveauCriticite.CRITIQUE, message, bassin));
    }

    public void verifierStockAliment(Aliment alimentCible, Double stockActuel) {
        if (alimentCible == null || alimentCible.getId() == null || stockActuel == null) {
            return;
        }

        Double seuilMin = parametreSystemeService.getDouble(ParametreSystemeService.STOCK_ALIMENT_MINIMUM_KG, null);
        if (seuilMin == null || stockActuel >= seuilMin) {
            return;
        }

        Aliment aliment = alimentRepository.findById(alimentCible.getId()).orElse(null);
        if (aliment == null) {
            return;
        }

        String message = "Stock de « " + aliment.getNom() + " » à " + arrondir(stockActuel)
                + " kg, sous le seuil minimal de " + seuilMin + " kg.";

        NiveauCriticite niveau = stockActuel <= 0
                ? NiveauCriticite.CRITIQUE
                : NiveauCriticite.AVERTISSEMENT;

        alerteService.creerSiNonExiste(AlerteCreateDTO.pourAliment(
                ModuleSource.ALIMENTATION, TypeAlerte.STOCK_BAS, niveau, message, aliment));
    }

    public void verifierRecolteProche(LotModels lot) {
        if (lot == null || lot.getEspece() == null || lot.getPoidsMoyenActuel() == null
                || lot.getEspece().getPoidsCibleMoyen() == null) {
            return;
        }

        Double ratio = parametreSystemeService.getDouble(ParametreSystemeService.SEUIL_PROCHE_RECOLTE_RATIO, null);
        if (ratio == null) {
            return;
        }

        double poidsCible = lot.getEspece().getPoidsCibleMoyen().doubleValue();
        if (poidsCible <= 0 || lot.getPoidsMoyenActuel() < poidsCible * ratio) {
            return;
        }

        int pourcentage = (int) Math.round(lot.getPoidsMoyenActuel() / poidsCible * 100);
        String message = "Le lot " + lot.getCode() + " atteint " + pourcentage
                + " % du poids cible de récolte (" + arrondir(poidsCible) + " g).";

        alerteService.creerSiNonExiste(AlerteCreateDTO.pourLot(
                ModuleSource.PRODUCTION, TypeAlerte.RECOLTE_PROCHE, NiveauCriticite.INFO, message, lot));
    }

    public void verifierMortalite(LotModels lot, int mortsCumules) {
        if (lot == null || lot.getEffectifInitial() == null || lot.getEffectifInitial() <= 0) {
            return;
        }

        Double tauxMax = parametreSystemeService.getDouble(ParametreSystemeService.TAUX_MORTALITE_MAXIMUM, null);
        if (tauxMax == null) {
            return;
        }

        double taux = (double) mortsCumules / lot.getEffectifInitial() * 100.0;
        if (taux < tauxMax) {
            return;
        }

        String message = "Taux de mortalité de " + arrondir(taux) + " % sur le lot " + lot.getCode()
                + " (" + mortsCumules + " morts sur " + lot.getEffectifInitial()
                + " individus), au-delà du seuil de " + tauxMax + " %.";

        alerteService.creerSiNonExiste(AlerteCreateDTO.pourLot(
                ModuleSource.PRODUCTION, TypeAlerte.MORTALITE_ELEVEE, NiveauCriticite.CRITIQUE, message, lot));
    }

    private void ajouter(StringBuilder anomalies, String texte) {
        if (anomalies.length() > 0) {
            anomalies.append(", ");
        }
        anomalies.append(texte);
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
