package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.admin.service.ParametreSystemeService;
import org.springframework.stereotype.Service;

@Service
public class AnalyseVerificationService {

    private final ParametreSystemeService parametreSystemeService;

    public AnalyseVerificationService(ParametreSystemeService parametreSystemeService) {
        this.parametreSystemeService = parametreSystemeService;
    }

    public void verifierTemperatureBassin(Long bassinId, Double temperatureActuelle) {
        Double seuilMax = parametreSystemeService.getDouble(ParametreSystemeService.TEMP_EAU_MAX, null);

        // Un seuil non configuré ne doit jamais déclencher d'alerte.
        if (seuilMax == null || temperatureActuelle == null) {
            return;
        }

        if (temperatureActuelle > seuilMax) {
            System.out.println("ALERT : Le bassin " + bassinId + " a dépassé le seuil de température de "
                    + seuilMax + " °C.");

            // C'est ici qu'on instanciera la table T4 (Alerte) une fois la création automatique implémentée.
        }
    }
}
