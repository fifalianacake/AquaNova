package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.alerte.models.SeuilAlerte;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AnalyseVerificationService {

    private final SeuilAlerteService seuilAlerteService;

    public AnalyseVerificationService(SeuilAlerteService seuilAlerteService) {
        this.seuilAlerteService = seuilAlerteService;
    }

    public void verifierTemperatureBassin(Long bassinId, Double temperatureActuelle) {
        Optional<SeuilAlerte> seuilOpt = seuilAlerteService.getSeuilActif("BASSIN_TEMP_MAX");

        // 2. Si le seuil n'existe pas ou qu'il a été désactivé (actif = false),
        if (seuilOpt.isEmpty()) {
            return; 
        }

        SeuilAlerte seuil = seuilOpt.get();

        // 3. Comparaison automatique avec la valeur configurée sans changer le code source
        if (temperatureActuelle > seuil.getValeur()) {
            System.out.println("ALERT : Le bassin " + bassinId + " a dépassé le seuil de " 
                    + seuil.getValeur() + " " + seuil.getUnite() + " (" + seuil.getLibelle() + ")");
            
            // C'est ici qu'on instance la table T4 (Alerte) pour l'enregistrer
        }
    }
}