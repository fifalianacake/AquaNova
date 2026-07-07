package mg.itu.aquanova.alerte.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.alerte.dto.AlerteDTO;
import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.repositories.AlerteRepository;
import mg.itu.aquanova.alerte.repositories.NiveauCriticiteRepository;
import mg.itu.aquanova.alerte.repositories.StatutAlerteRepository;
import mg.itu.aquanova.alerte.repositories.TypeAlerteRepository;

@Service
public class AlerteService {

    private final AlerteRepository alerteRepository;
    private final TypeAlerteRepository typeAlerteRepository;
    private final NiveauCriticiteRepository niveauCriticiteRepository;
    private final StatutAlerteRepository statutAlerteRepository;

    public AlerteService(AlerteRepository alerteRepository,
                         TypeAlerteRepository typeAlerteRepository,
                         NiveauCriticiteRepository niveauCriticiteRepository,
                         StatutAlerteRepository statutAlerteRepository) {
        this.alerteRepository = alerteRepository;
        this.typeAlerteRepository = typeAlerteRepository;
        this.niveauCriticiteRepository = niveauCriticiteRepository;
        this.statutAlerteRepository = statutAlerteRepository;
    }

    // Convertir Alerte → AlerteDTO
    private AlerteDTO toDTO(Alerte a) {
        AlerteDTO dto = new AlerteDTO();
        dto.setId(a.getId());
        dto.setModuleSource(a.getModuleSource());
        dto.setTypeAlerteCode(a.getTypeAlerte().getCode());
        dto.setTypeAlerte(a.getTypeAlerte().getLibelle());
        dto.setNiveauCriticiteCode(a.getNiveauCriticite().getCode());
        dto.setNiveauCriticite(a.getNiveauCriticite().getLibelle());
        dto.setNiveauOrdre(a.getNiveauCriticite().getOrdre());
        dto.setStatutAlerteCode(a.getStatutAlerte().getCode());
        dto.setStatutAlerte(a.getStatutAlerte().getLibelle());
        dto.setMessage(a.getMessage());
        dto.setEntiteType(a.getEntiteType());
        dto.setEntiteId(a.getEntiteId());
        dto.setDateCreation(a.getDateCreation());
        dto.setDateResolution(a.getDateResolution());
        dto.setCommentaireResolution(a.getCommentaireResolution());
        return dto;
    }

    // Toutes les alertes actives
    public List<AlerteDTO> getAlertesActives() {
        return this.alerteRepository.findAlertesActives()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Alertes critiques uniquement
    public List<AlerteDTO> getAlertesCritiquesActives() {
        return this.alerteRepository.findAlertesCritiquesActives()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Compte badge critiques
    public Long countCritiques() {
        return this.alerteRepository.countAlertesCritiquesActives();
    }

    // Détail d'une alerte
    public AlerteDTO getById(Long id) {
        Alerte a = this.alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + id));
        return this.toDTO(a);
    }

    // Recherche avec filtres + pagination
    public Page<AlerteDTO> search(AlerteFilterDTO filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getTaille());

        LocalDateTime dateDebut = filter.getDateDebut() != null
                ? filter.getDateDebut().atStartOfDay() : null;
        LocalDateTime dateFin = filter.getDateFin() != null
                ? filter.getDateFin().atTime(23, 59, 59) : null;

        String motCle = (filter.getMotCle() != null && !filter.getMotCle().isBlank())
                ? filter.getMotCle() : null;
        String module = (filter.getModuleSource() != null && !filter.getModuleSource().isBlank())
                ? filter.getModuleSource() : null;
        String type = (filter.getTypeAlerte() != null && !filter.getTypeAlerte().isBlank())
                ? filter.getTypeAlerte() : null;
        String niveau = (filter.getNiveauCriticite() != null && !filter.getNiveauCriticite().isBlank())
                ? filter.getNiveauCriticite() : null;
        String statut = (filter.getStatutAlerte() != null && !filter.getStatutAlerte().isBlank())
                ? filter.getStatutAlerte() : null;

        return this.alerteRepository.searchAlertes(
                module, type, niveau, statut,
                dateDebut, dateFin, motCle, pageable)
                .map(this::toDTO);
    }
}
