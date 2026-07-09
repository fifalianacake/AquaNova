package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.alerte.dto.AlerteDTO;
import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.dto.UpdateStatutAlerteDTO;
import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.repositories.AlerteRepository;
import mg.itu.aquanova.alerte.repositories.HistoriqueAlerteRepository;
import mg.itu.aquanova.alerte.repositories.NiveauCriticiteRepository;
import mg.itu.aquanova.alerte.repositories.StatutAlerteRepository;
import mg.itu.aquanova.alerte.repositories.TypeAlerteRepository;
import mg.itu.aquanova.export_pdf.models.ListePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlerteService {
  
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlerteRepository alerteRepository;
    private final PdfExportService pdfExportService;
    private final TypeAlerteRepository typeAlerteRepository;
    private final NiveauCriticiteRepository niveauCriticiteRepository;
    private final StatutAlerteRepository statutAlerteRepository;
    private final HistoriqueAlerteService historiqueAlerteService;

    public AlerteService(AlerteRepository alerteRepository,
                         TypeAlerteRepository typeAlerteRepository,
                         NiveauCriticiteRepository niveauCriticiteRepository,
                         StatutAlerteRepository statutAlerteRepository,
                         HistoriqueAlerteService historiqueAlerteService,
                         PdfExportService pdfExportService) {
        this.alerteRepository = alerteRepository;
        this.typeAlerteRepository = typeAlerteRepository;
        this.niveauCriticiteRepository = niveauCriticiteRepository;
        this.statutAlerteRepository = statutAlerteRepository;
        this.historiqueAlerteService = historiqueAlerteService;
        this.pdfExportService = pdfExportService;
    }
  
      /**
     * Recherche paginée dans l'historique des alertes avec filtres dynamiques.
     */
    public Page<Alerte> searchHistorique(AlerteFilterDTO filter, Pageable pageable) {
        return alerteRepository.findAll(buildSpecification(filter), pageable);
    }

    /**
     * Export PDF de l'historique des alertes (toutes les lignes correspondant aux filtres).
     */
    public byte[] exportHistoriquePdf(AlerteFilterDTO filter) {
        List<Alerte> alertes = alerteRepository.findAll(buildSpecification(filter));

        List<String> colonnes = List.of(
                "Date création", "Date résolution", "Module", "Type",
                "Criticité", "Statut", "Message");

        List<List<String>> lignes = new ArrayList<>();
        for (Alerte a : alertes) {
            lignes.add(List.of(
                    formatDate(a.getDateCreation()),
                    formatDate(a.getDateResolution()),
                    a.getModuleSource() != null ? a.getModuleSource().name() : "-",
                    a.getTypeAlerte() != null ? a.getTypeAlerte().name() : "-",
                    a.getNiveauCriticite() != null ? a.getNiveauCriticite().name() : "-",
                    a.getStatut() != null ? a.getStatut().name() : "-",
                    a.getMessage() != null ? a.getMessage() : "-"));
        }

        ListePdfData data = ListePdfData.of("Historique des alertes")
                .filtre("Module", filter.getModuleSource())
                .filtre("Type", filter.getTypeAlerte())
                .filtre("Criticité", filter.getNiveauCriticite())
                .filtre("Statut", filter.getStatut())
                .filtre("Date début", filter.getDateDebut())
                .filtre("Date fin", filter.getDateFin())
                .filtre("Lot", filter.getLotId())
                .filtre("Bassin", filter.getBassinId())
                .colonnes(colonnes)
                .lignes(lignes)
                .total("Total alertes", String.valueOf(alertes.size()));

        return pdfExportService.genererListe(data);
    }

    // ── Construction de la Specification dynamique ──

    private Specification<Alerte> buildSpecification(AlerteFilterDTO filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }

            if (filter.getModuleSource() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("moduleSource"), filter.getModuleSource()));
            }
            if (filter.getTypeAlerte() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("typeAlerte"), filter.getTypeAlerte()));
            }
            if (filter.getNiveauCriticite() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("niveauCriticite"), filter.getNiveauCriticite()));
            }
            if (filter.getStatut() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("statut"), filter.getStatut()));
            }
            if (filter.getDateDebut() != null) {
                LocalDateTime debut = filter.getDateDebut().atStartOfDay();
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateCreation"), debut));
            }
            if (filter.getDateFin() != null) {
                LocalDateTime fin = filter.getDateFin().atTime(LocalTime.MAX);
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateCreation"), fin));
            }
            if (filter.getLotId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("lot").get("id"), filter.getLotId()));
            }
            if (filter.getBassinId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bassin").get("id"), filter.getBassinId()));
            }

            return predicates;
        };
    }

    private String formatDate(LocalDateTime dt) {
        return dt != null ? dt.format(FMT) : "-";
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

    // ============ NOUVELLES FONCTIONS POUR LA RESOLUTION ============

    @Transactional
    public void changerStatut(Long idAlerte, UpdateStatutAlerteDTO dto) {
        Alerte alerte = this.alerteRepository.findById(idAlerte)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + idAlerte));

        String ancienStatut = alerte.getStatutAlerte().getCode();
        String nouveauStatut = dto.getNouveauStatut();

        // Vérifier que le nouveau statut existe
        StatutAlerte statut = this.statutAlerteRepository.findByCode(nouveauStatut)
                .orElseThrow(() -> new RuntimeException("Statut inconnu : " + nouveauStatut));

        // Ne pas autoriser le changement si déjà résolue ou ignorée
        if (alerte.getStatutAlerte().getCode().equals("RESOLUE") ||
            alerte.getStatutAlerte().getCode().equals("IGNOREE")) {
            throw new RuntimeException("Cette alerte est déjà clôturée et ne peut plus être modifiée");
        }

        // Mettre à jour le statut
        alerte.setStatutAlerte(statut);

        // Si le nouveau statut est RESOLUE, enregistrer la date de résolution
        if (nouveauStatut.equals("RESOLUE")) {
            alerte.setDateResolution(LocalDateTime.now());
            // Le commentaire est obligatoire pour RESOLUE
            if (dto.getCommentaire() == null || dto.getCommentaire().isBlank()) {
                throw new RuntimeException("Un commentaire est obligatoire pour résoudre une alerte");
            }
        }

        // Si le nouveau statut est IGNOREE, enregistrer le commentaire
        if (nouveauStatut.equals("IGNOREE")) {
            // Le commentaire est conseillé pour IGNOREE
            if (dto.getCommentaire() == null || dto.getCommentaire().isBlank()) {
                throw new RuntimeException("Un commentaire est obligatoire pour ignorer une alerte");
            }
        }

        // Enregistrer le commentaire de résolution
        if (dto.getCommentaire() != null && !dto.getCommentaire().isBlank()) {
            alerte.setCommentaireResolution(dto.getCommentaire());
        }

        // Sauvegarder l'alerte
        this.alerteRepository.save(alerte);

        // Enregistrer l'historique
        this.historiqueAlerteService.enregistrerChangement(
                alerte, ancienStatut, nouveauStatut, dto.getCommentaire()
        );
    }

    @Transactional
    public void marquerEnCours(Long idAlerte) {
        Alerte alerte = this.alerteRepository.findById(idAlerte)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + idAlerte));

        if (alerte.getStatutAlerte().getCode().equals("RESOLUE") ||
            alerte.getStatutAlerte().getCode().equals("IGNOREE")) {
            throw new RuntimeException("Cette alerte est déjà clôturée");
        }

        String ancienStatut = alerte.getStatutAlerte().getCode();
        StatutAlerte statut = this.statutAlerteRepository.findByCode("EN_COURS")
                .orElseThrow(() -> new RuntimeException("Statut EN_COURS introuvable"));

        alerte.setStatutAlerte(statut);
        this.alerteRepository.save(alerte);

        this.historiqueAlerteService.enregistrerChangement(
                alerte, ancienStatut, "EN_COURS", "Prise en charge de l'alerte"
        );
    }

    @Transactional
    public void marquerCommeResolue(Long idAlerte, String commentaire) {
        UpdateStatutAlerteDTO dto = new UpdateStatutAlerteDTO("RESOLUE", commentaire);
        this.changerStatut(idAlerte, dto);
    }

    @Transactional
    public void ignorerAlerte(Long idAlerte, String commentaire) {
        UpdateStatutAlerteDTO dto = new UpdateStatutAlerteDTO("IGNOREE", commentaire);
        this.changerStatut(idAlerte, dto);
    }
}
