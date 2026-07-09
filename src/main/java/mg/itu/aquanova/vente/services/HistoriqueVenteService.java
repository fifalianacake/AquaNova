package mg.itu.aquanova.vente.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;

@Service
public class HistoriqueVenteService {
    private final VenteService venteService;
    private final VenteRepository venteRepository;
    private final StatutVenteRepository statutVenteRepository;

    public HistoriqueVenteService(VenteService venteService, VenteRepository venteRepository,
            StatutVenteRepository statutVenteRepository) {
        this.venteService = venteService;
        this.venteRepository = venteRepository;
        this.statutVenteRepository = statutVenteRepository;
    }

    public List<Vente> searchTransactions(TransactionFilterDTO filters) {
        return venteService.search(filters);
    }

    public Vente getJournalByVente(Long venteId) {
        return venteRepository.findById(venteId)
                .orElseThrow(() -> new RuntimeException("Vente not found with id: " + venteId));
    }

    public List<Vente> getActiveVentesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        TransactionFilterDTO filters = new TransactionFilterDTO();
        filters.setDateDebut(dateDebut);
        filters.setDateFin(dateFin);
        return searchTransactions(filters);
    }

    public List<StatutVente> getAllStatuts() {
        return statutVenteRepository.findAll();
    }
}
