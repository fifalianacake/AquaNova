package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.repositories.ClientRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final VenteRepository venteRepository; 

    public ClientService(ClientRepository clientRepository, VenteRepository venteRepository) {
        this.clientRepository = clientRepository;
        this.venteRepository = venteRepository;
    }

    public List<Client> rechercher(Long id, String nom, Long typeId, String contact, Boolean actif) {
        return clientRepository.filtrerClients(id, likePattern(nom), typeId, likePattern(contact), actif);
    }

    public Client trouverParId(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client introuvable"));
    }

    @Transactional
    public Client enregistrer(Client client) {
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client est obligatoire.");
        }
        if (client.getTypeClient() == null || client.getTypeClient().getId() == null) {
            throw new IllegalArgumentException("Le type de client est obligatoire.");
        }
        if (client.getEmail() != null && !client.getEmail().isEmpty() && !client.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'adresse email n'est pas valide.");
        }
        return clientRepository.save(client);
    }

    @Transactional
    public void supprimerOuDesactiver(Long id) {
        Client client = trouverParId(id);
        
        List<Vente> ventesDuClient = venteRepository.findByClientId(id);

        if (!ventesDuClient.isEmpty()) {
            client.setActif(false);
            clientRepository.save(client);
            System.out.println("CLIENT_LOG: Client '" + client.getNom() + "' désactivé (Historique de ventes présent).");
        } else {
            clientRepository.delete(client);
            System.out.println("CLIENT_LOG: Client '" + client.getNom() + "' supprimé physiquement.");
        }
    }

    public List<Vente> obtenirHistoriqueVentes(Long clientId) {
        return venteRepository.findByClientId(clientId);
    }

    public Double calculerChiffreAffaires(Long clientId) {
        return obtenirHistoriqueVentes(clientId).stream()
                .filter(v -> v.getStatutVente() != null && 
                            (StatutVenteEnum.VALIDEE == v.getStatutVente().getCode() || 
                             StatutVenteEnum.PAYEE == v.getStatutVente().getCode()))
                .mapToDouble(v -> v.getPoidsVendu() * v.getPrixUnitaire()) // Sécurité si montant_total n'est pas encore calculé
                .sum();
    }

    private String likePattern(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return "%" + value.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
