package mg.itu.aquanova.vente.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;
import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/clients")
public class ClientExportController {

    private final ClientService clientService;
    private final VenteService venteService;
    private final PdfRenderService pdfRenderService;

    public ClientExportController(ClientService clientService,
            VenteService venteService,
            PdfRenderService pdfRenderService) {
        this.clientService = clientService;
        this.venteService = venteService;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exporterFicheClient(@PathVariable Long id) {
        Client client = clientService.trouverParId(id);
        List<Vente> ventes = venteService.getByClient(id);

        // Les ventes annulées restent affichées mais ne comptent pas dans le chiffre d'affaires.
        List<Vente> retenues = ventes.stream()
                .filter(v -> v.getStatutVente() == null
                        || StatutVenteEnum.ANNULEE != v.getStatutVente().getCode())
                .toList();

        double totalPoids = retenues.stream()
                .mapToDouble(v -> v.getPoidsVendu() != null ? v.getPoidsVendu() : 0).sum();
        double totalMontant = retenues.stream()
                .mapToDouble(v -> v.getMontantTotal() != null ? v.getMontantTotal() : 0).sum();

        Map<String, Object> modele = new HashMap<>();
        modele.put("client", client);
        modele.put("ventes", ventes);
        modele.put("nbVentes", retenues.size());
        modele.put("totalPoids", totalPoids);
        modele.put("totalMontant", totalMontant);
        modele.put("panierMoyen", retenues.isEmpty() ? 0.0 : totalMontant / retenues.size());
        modele.put("prixMoyenKg", totalPoids > 0 ? totalMontant / totalPoids : 0.0);
        modele.put("sousTitre", client.getTypeClient() != null ? client.getTypeClient().getLibelle() : null);

        byte[] pdf = pdfRenderService.rendre("client", modele);
        return PdfResponses.attachment(pdf,
                "client-" + client.getId() + "-" + client.getNom().replace(" ", "_") + ".pdf");
    }
}
