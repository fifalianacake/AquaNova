package mg.itu.aquanova.alerte.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.HistoriqueAlerte;
import mg.itu.aquanova.alerte.repositories.HistoriqueAlerteRepository;

@Service
public class HistoriqueAlerteService {

    private final HistoriqueAlerteRepository repository;

    public HistoriqueAlerteService(
            HistoriqueAlerteRepository repository) {

        this.repository = repository;
    }

    public void enregistrer(
            Alerte alerte,
            String ancien,
            String nouveau,
            String commentaire) {

        HistoriqueAlerte historique =
                new HistoriqueAlerte();

        historique.setAlerte(alerte);
        historique.setAncienStatut(ancien);
        historique.setNouveauStatut(nouveau);
        historique.setCommentaire(commentaire);
        historique.setDateChangement(LocalDateTime.now());

        repository.save(historique);

    }

}