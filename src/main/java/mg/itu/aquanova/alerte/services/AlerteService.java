package mg.itu.aquanova.alerte.services;

import mg.itu.aquanova.alerte.dto.AlerteCreateDTO;
import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.dto.UpdateStatutAlerteDTO;
import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.StatutAlerte;
import mg.itu.aquanova.alerte.repositories.AlerteRepository;
import mg.itu.aquanova.export_pdf.models.ListePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlerteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<StatutAlerte> STATUTS_CLOTURES = List.of(StatutAlerte.RESOLUE, StatutAlerte.IGNOREE);

    private final AlerteRepository alerteRepository;
    private final PdfExportService pdfExportService;
    private final HistoriqueAlerteService historiqueAlerteService;

    public AlerteService(AlerteRepository alerteRepository,
                         HistoriqueAlerteService historiqueAlerteService,
                         PdfExportService pdfExportService) {
        this.alerteRepository = alerteRepository;
        this.historiqueAlerteService = historiqueAlerteService;
        this.pdfExportService = pdfExportService;
    }

    public Page<Alerte> searchHistorique(AlerteFilterDTO filter, Pageable pageable) {
        return alerteRepository.findAll(specificationHistorique(filter), pageable);
    }

    public Page<Alerte> search(AlerteFilterDTO filter, Pageable pageable) {
        Specification<Alerte> spec = buildSpecification(filter).and(
                (root, query, cb) -> cb.not(root.get("statut").in(STATUTS_CLOTURES)));
        return alerteRepository.findAll(spec, pageable);
    }

    private Specification<Alerte> specificationHistorique(AlerteFilterDTO filter) {
        return buildSpecification(filter).and(
                (root, query, cb) -> root.get("statut").in(STATUTS_CLOTURES));
    }

    public byte[] exportHistoriquePdf(AlerteFilterDTO filter) {
        List<Alerte> alertes = alerteRepository.findAll(specificationHistorique(filter));

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

    public List<Alerte> getAlertesActives() {
        return alerteRepository.findByStatutNotInOrderByDateCreationDesc(STATUTS_CLOTURES);
    }

    public List<Alerte> getAlertesCritiquesActives() {
        return alerteRepository.findByNiveauCriticiteAndStatutNotInOrderByDateCreationDesc(
                NiveauCriticite.CRITIQUE, STATUTS_CLOTURES);
    }

    public long countCritiques() {
        return alerteRepository.countByNiveauCriticiteAndStatutNotIn(NiveauCriticite.CRITIQUE, STATUTS_CLOTURES);
    }

    public Alerte getById(Long id) {
        return alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + id));
    }

    @Transactional
    public void changerStatut(Long idAlerte, UpdateStatutAlerteDTO dto) {
        Alerte alerte = alerteRepository.findById(idAlerte)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + idAlerte));

        StatutAlerte ancienStatut = alerte.getStatut();
        StatutAlerte nouveauStatut = dto.getNouveauStatut();

        if (nouveauStatut == null) {
            throw new RuntimeException("Le nouveau statut est obligatoire");
        }

        if (STATUTS_CLOTURES.contains(ancienStatut)) {
            throw new RuntimeException("Cette alerte est déjà clôturée et ne peut plus être modifiée");
        }

        if (STATUTS_CLOTURES.contains(nouveauStatut)
                && (dto.getCommentaire() == null || dto.getCommentaire().isBlank())) {
            throw new RuntimeException("Un commentaire est obligatoire pour résoudre ou ignorer une alerte");
        }

        alerte.setStatut(nouveauStatut);

        if (nouveauStatut == StatutAlerte.RESOLUE) {
            alerte.setDateResolution(LocalDateTime.now());
        }

        alerteRepository.save(alerte);

        historiqueAlerteService.enregistrer(alerte, ancienStatut.name(), nouveauStatut.name(), dto.getCommentaire());
    }

    @Transactional
    public void marquerEnCours(Long idAlerte) {
        Alerte alerte = alerteRepository.findById(idAlerte)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable : " + idAlerte));

        if (STATUTS_CLOTURES.contains(alerte.getStatut())) {
            throw new RuntimeException("Cette alerte est déjà clôturée");
        }
        if (alerte.getStatut() == StatutAlerte.EN_COURS) {
            throw new RuntimeException("Cette alerte est déjà prise en charge");
        }

        StatutAlerte ancienStatut = alerte.getStatut();
        alerte.setStatut(StatutAlerte.EN_COURS);
        alerteRepository.save(alerte);

        historiqueAlerteService.enregistrer(alerte, ancienStatut.name(), StatutAlerte.EN_COURS.name(),
                "Prise en charge de l'alerte");
    }

    @Transactional
    public void marquerCommeResolue(Long idAlerte, String commentaire) {
        changerStatut(idAlerte, new UpdateStatutAlerteDTO(StatutAlerte.RESOLUE, commentaire));
    }

    @Transactional
    public void ignorerAlerte(Long idAlerte, String commentaire) {
        changerStatut(idAlerte, new UpdateStatutAlerteDTO(StatutAlerte.IGNOREE, commentaire));
    }

    @Transactional
    public Alerte creerAlerte(AlerteCreateDTO dto){
        validerCreation(dto);

        Alerte alerte = new Alerte();

        alerte.setModuleSource(dto.getModuleSource());
        alerte.setTypeAlerte(dto.getTypeAlerte());
        alerte.setNiveauCriticite(dto.getNiveauCriticite());
        alerte.setMessage(dto.getMessage());
        alerte.setLot(dto.getLot());
        alerte.setBassin(dto.getBassin());
        alerte.setAliment(dto.getAliment());

        alerte.setStatut(StatutAlerte.ACTIVE);
        alerte.setDateCreation(LocalDateTime.now());

        return alerteRepository.save(alerte);
    }

    /** Règles métier : type, criticité, module et message sont obligatoires (le statut est posé ici). */
    private void validerCreation(AlerteCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("L'alerte est obligatoire.");
        }
        if (dto.getModuleSource() == null) {
            throw new IllegalArgumentException("Le module source de l'alerte est obligatoire.");
        }
        if (dto.getTypeAlerte() == null) {
            throw new IllegalArgumentException("Le type de l'alerte est obligatoire.");
        }
        if (dto.getNiveauCriticite() == null) {
            throw new IllegalArgumentException("Le niveau de criticité de l'alerte est obligatoire.");
        }
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new IllegalArgumentException("Le message de l'alerte est obligatoire.");
        }
    }

    @Transactional
    public Alerte creerSiNonExiste(AlerteCreateDTO dto){
        validerCreation(dto);

        Optional<Alerte> alerteExistante =
                alerteRepository.findFirstByModuleSourceAndTypeAlerteAndLotAndBassinAndAlimentAndStatutNotIn(
                        dto.getModuleSource(),
                        dto.getTypeAlerte(),
                        dto.getLot(),
                        dto.getBassin(),
                        dto.getAliment(),
                        STATUTS_CLOTURES
                );

        if(alerteExistante.isPresent()){
            return alerteExistante.get();
        }

        return creerAlerte(dto);
    }
}
