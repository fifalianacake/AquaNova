package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import mg.itu.aquanova.achat.dto.DepenseFilter;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.models.DepensePaiement;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.DepenseRepository;

@Service
public class DepenseService {

    private final DepenseRepository depenseRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;

    public DepenseService(
            DepenseRepository depenseRepository,
            CategorieDepenseRepository categorieDepenseRepository) {
        this.depenseRepository = depenseRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
    }

    public Page<Depense> lister(DepenseFilter filter, Pageable pageable) {
        return depenseRepository.findAll(specification(filter), pageable);
    }

    public List<Depense> listerPourExport(DepenseFilter filter) {
        return depenseRepository.findAll(specification(filter));
    }

    public Depense trouverParId(Long id) {
        return depenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dépense introuvable : " + id));
    }

    @Transactional
    public Depense enregistrer(Depense depense) {
        if (depense == null) {
            throw new IllegalArgumentException("La dépense est obligatoire.");
        }
        if (depense.getPaiements() != null && !depense.getPaiements().isEmpty()) {
            depense.reglerTotalPaiements();
        }

        valider(depense);
        normaliser(depense);
        normaliserPaiements(depense);
        return depenseRepository.save(depense);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!depenseRepository.existsById(id)) {
            throw new EntityNotFoundException("Dépense introuvable : " + id);
        }
        depenseRepository.deleteById(id);
    }

    public BigDecimal calculerTotalFiltre(DepenseFilter filter) {
        return listerPourExport(filter).stream()
                .map(depense -> depense.getMontant())
                .filter(montant -> montant != null)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public byte[] exporterPdf(DepenseFilter filter) {
        List<Depense> depenses = listerPourExport(filter);
        BigDecimal total = depenses.stream()
                .map(depense -> depense.getMontant())
                .filter(montant -> montant != null)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        StringBuilder contenu = new StringBuilder();
        contenu.append("DEPENSES DIVERSES\n");
        contenu.append("Export filtre - ").append(LocalDate.now()).append("\n\n");
        for (Depense depense : depenses) {
            contenu.append("#").append(depense.getId())
                    .append(" | ").append(depense.getDateDepense())
                    .append(" | ").append(depense.getCategorieDepense() != null ? depense.getCategorieDepense().getLibelle() : "-")
                    .append(" | ").append(depense.getLibelle())
                    .append(" | ").append(depense.getMontant())
                    .append(" | ").append(nullToDash(depense.getModePaiement()))
                    .append(" | ").append(nullToDash(depense.getReference()))
                    .append("\n");
        }
        contenu.append("\nTOTAL : ").append(total);

        return creerPdfSimple(contenu.toString());
    }

    private Specification<Depense> specification(DepenseFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter == null) {
                return predicates;
            }
            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }
            if (filter.getCategorieDepenseId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categorieDepense").get("id"), filter.getCategorieDepenseId()));
            }
            if (filter.getDateDebut() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dateDepense"), filter.getDateDebut()));
            }
            if (filter.getDateFin() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("dateDepense"), filter.getDateFin()));
            }
            if (filter.getMontantMin() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("montant"), filter.getMontantMin()));
            }
            if (filter.getMontantMax() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("montant"), filter.getMontantMax()));
            }
            if (filter.getModePaiement() != null && !filter.getModePaiement().isBlank()) {
                var paiements = root.join("paiements", JoinType.LEFT);
                predicates = cb.and(predicates, cb.like(cb.lower(paiements.get("modePaiement").as(String.class)), "%" + filter.getModePaiement().trim().toLowerCase() + "%"));
            }

            return predicates;
        };
    }

    private void valider(Depense depense) {
        if (depense == null) {
            throw new IllegalArgumentException("La dépense est obligatoire.");
        }
        if (depense.getDateDepense() == null) {
            throw new IllegalArgumentException("La date est obligatoire.");
        }
        if (depense.getCategorieDepense() == null || depense.getCategorieDepense().getId() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire.");
        }
        if (depense.getLibelle() == null || depense.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire.");
        }

        if (depense.getPaiements() == null || depense.getPaiements().isEmpty()) {
            if (depense.getMontant() == null || depense.getMontant().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le montant doit être supérieur ou égal à zéro.");
            }
        } else {
            boolean anyPayment = false;
            for (DepensePaiement paiement : depense.getPaiements()) {
                if (paiement == null) {
                    continue;
                }
                if (paiement.getModePaiement() == null) {
                    throw new IllegalArgumentException("Le mode de paiement de chaque ligne est obligatoire.");
                }
                if (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Le montant de chaque ligne de paiement doit être supérieur à zéro.");
                }
                anyPayment = true;
            }
            if (!anyPayment) {
                throw new IllegalArgumentException("Au moins une ligne de paiement valide est requise.");
            }
        }
    }

    private void normaliser(Depense depense) {
        CategorieDepense categorie = categorieDepenseRepository.findById(depense.getCategorieDepense().getId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + depense.getCategorieDepense().getId()));

        depense.setCategorieDepense(categorie);
        depense.setLibelle(depense.getLibelle().trim());
        depense.setReference(blankToNull(depense.getReference()));
        depense.setObservation(blankToNull(depense.getObservation()));

        if (depense.getPaiements() != null && !depense.getPaiements().isEmpty()) {
            depense.setModePaiement(null);
        } else {
            depense.setModePaiement(blankToNull(depense.getModePaiement()));
        }
    }

    private void normaliserPaiements(Depense depense) {
        if (depense.getPaiements() == null) {
            return;
        }
        depense.getPaiements().removeIf(paiement -> paiement == null || montantVide(paiement));
        for (DepensePaiement paiement : depense.getPaiements()) {
            paiement.setDepense(depense);
            paiement.setReference(blankToNull(paiement.getReference()));
            paiement.setObservation(blankToNull(paiement.getObservation()));
        }
    }

    private boolean montantVide(DepensePaiement paiement) {
        return paiement.getModePaiement() == null
                && (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) == 0)
                && (paiement.getReference() == null || paiement.getReference().isBlank())
                && (paiement.getObservation() == null || paiement.getObservation().isBlank());
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private byte[] creerPdfSimple(String texte) {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n/F1 10 Tf\n50 790 Td\n14 TL\n");
        for (String ligne : texte.split("\\R")) {
            stream.append("(").append(echapperPdf(ligne)).append(") Tj\nT*\n");
        }
        stream.append("ET");

        byte[] streamBytes = stream.toString().getBytes(StandardCharsets.ISO_8859_1);
        String header = "%PDF-1.4\n";
        String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
        String obj2 = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n";
        String obj3 = "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n";
        String obj4 = "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n";
        String obj5Prefix = "5 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n";
        String obj5Suffix = "\nendstream\nendobj\n";

        String[] objets = {obj1, obj2, obj3, obj4, obj5Prefix + stream + obj5Suffix};
        StringBuilder pdf = new StringBuilder(header);
        int[] offsets = new int[objets.length + 1];
        for (int i = 0; i < objets.length; i++) {
            offsets[i + 1] = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
            pdf.append(objets[i]);
        }
        int xrefOffset = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        pdf.append("xref\n0 ").append(objets.length + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (int i = 1; i < offsets.length; i++) {
            pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        }
        pdf.append("trailer\n<< /Size ").append(objets.length + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefOffset).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private String echapperPdf(String value) {
        return value.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }
}
