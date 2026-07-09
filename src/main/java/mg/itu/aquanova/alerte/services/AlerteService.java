package mg.itu.aquanova.alerte.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.alerte.dto.AlerteFilterDTO;
import mg.itu.aquanova.alerte.models.Alerte;
import mg.itu.aquanova.alerte.repositories.AlerteRepository;
import mg.itu.aquanova.export_pdf.models.ListePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;

/**
 * Service métier de l'historique des alertes.
 * <p>
 * L'historique sert uniquement à l'analyse : aucune suppression n'est exposée.
 * Les deux opérations principales sont la recherche paginée et l'export PDF.
 */
@Service
public class AlerteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlerteRepository alerteRepository;
    private final PdfExportService pdfExportService;

    public AlerteService(AlerteRepository alerteRepository, PdfExportService pdfExportService) {
        this.alerteRepository = alerteRepository;
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
}
