package mg.itu.aquanova.vente.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;
import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/ventes")
public class VenteExportController {

    private final VenteService venteService;
    private final PdfRenderService pdfRenderService;

    public VenteExportController(VenteService venteService, PdfRenderService pdfRenderService) {
        this.venteService = venteService;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exporterFacture(@PathVariable Long id) {
        Vente vente = venteService.trouverParId(id);

        Map<String, Object> modele = new HashMap<>();
        modele.put("vente", vente);
        modele.put("sousTitre", vente.getClient() != null ? "Client : " + vente.getClient().getNom() : null);

        byte[] pdf = pdfRenderService.rendre("vente", modele);
        return PdfResponses.attachment(pdf, "facture-vente-" + vente.getId() + ".pdf");
    }

    @GetMapping("/historique/export-pdf")
    public ResponseEntity<byte[]> exporterHistorique(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long statutId,
            @RequestParam(required = false) String clientNom) {

        TransactionFilterDTO filter = new TransactionFilterDTO();
        filter.setClient(clientNom);
        filter.setDateDebut(dateDebut);
        filter.setDateFin(dateFin);
        filter.setStatutId(statutId);

        List<Vente> ventes = venteService.listerPourExport(filter);
        if (clientId != null) {
            ventes = ventes.stream()
                    .filter(v -> v.getClient() != null && clientId.equals(v.getClient().getId()))
                    .toList();
        }

        // Les ventes annulées restent visibles dans la liste mais sont exclues des totaux :
        // même règle que sur la page web et que dans le module Finance.
        List<Vente> retenues = ventes.stream().filter(this::estRetenue).toList();
        double totalPoids = retenues.stream()
                .mapToDouble(v -> v.getPoidsVendu() != null ? v.getPoidsVendu() : 0).sum();
        double totalMontant = retenues.stream()
                .mapToDouble(v -> v.getMontantTotal() != null ? v.getMontantTotal() : 0).sum();

        Map<String, String> filtres = new LinkedHashMap<>();
        if (dateDebut != null) {
            filtres.put("Du", dateDebut.toString());
        }
        if (dateFin != null) {
            filtres.put("Au", dateFin.toString());
        }
        if (clientNom != null && !clientNom.isBlank()) {
            filtres.put("Client", clientNom);
        }

        Map<String, Object> modele = new HashMap<>();
        modele.put("ventes", ventes);
        modele.put("filtres", filtres);
        modele.put("nbVentes", retenues.size());
        modele.put("totalPoids", totalPoids);
        modele.put("totalMontant", totalMontant);
        modele.put("sousTitre", construireSousTitrePeriode(dateDebut, dateFin));

        byte[] pdf = pdfRenderService.rendre("ventes-historique", modele);
        return PdfResponses.attachment(pdf, "historique-ventes.pdf");
    }

    private boolean estRetenue(Vente vente) {
        return vente.getStatutVente() == null
                || StatutVenteEnum.ANNULEE != vente.getStatutVente().getCode();
    }

    private String construireSousTitrePeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null && dateFin == null) {
            return "Toutes périodes confondues";
        }
        return "Période : " + (dateDebut != null ? dateDebut : "origine")
                + " au " + (dateFin != null ? dateFin : "aujourd'hui");
    }
}
