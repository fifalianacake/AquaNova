package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.dto.ClientFilter;
import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.repositories.ClientRepository;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<Client> lister(ClientFilter filter, Pageable pageable) {
        return clientRepository.findAll(specification(filter), pageable);
    }

    private Specification<Client> specification(ClientFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getNom() != null && !filter.getNom().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nom")), likePattern(filter.getNom())));
            }
            if (filter.getTypeId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("typeClient").get("id"), filter.getTypeId()));
            }
            if (filter.getContact() != null && !filter.getContact().isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("contact")), likePattern(filter.getContact())));
            }
            if (filter.getActif() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("actif"), filter.getActif()));
            }

            return predicates;
        };
    }

    /**
     * Clients actifs, utilisables pour une nouvelle sélection (ex: liste déroulante de vente).
     * Si {@code clientActuel} est désactivé, il est tout de même inclus pour ne pas le faire
     * disparaître d'un formulaire d'édition qui le référence déjà.
     */
    public List<Client> listerActifsPour(Client clientActuel) {
        List<Client> actifs = new java.util.ArrayList<>(rechercher(null, null, null, null, true));
        if (clientActuel != null && clientActuel.getId() != null
                && !Boolean.TRUE.equals(clientActuel.getActif())
                && actifs.stream().noneMatch(c -> c.getId().equals(clientActuel.getId()))) {
            actifs.add(clientActuel);
        }
        return actifs;
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
