package mg.itu.aquanova.vente.controllers;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/ventes")
public class VenteExportController {

    private final VenteService venteService;
    private final PdfExportService pdfService;

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER_COURT = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VenteExportController(VenteService venteService, PdfExportService pdfService) {
        this.venteService = venteService;
        this.pdfService = pdfService;
    }

   
    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exporterFacture(@PathVariable Long id) {
        Vente vente = venteService.trouverParId(id);

        String statut = vente.getStatutVente() != null ? 
                vente.getStatutVente().getCode().name() : "-";

        FichePdfData data = FichePdfData.of("FACTURE DE VENTE N° " + vente.getId())
            .sousTitre("Statut : " + statut)
            
            // Section 1 : Informations generale
            .section("Informations générales", Map.of(
                "Date vente", vente.getDateVente() != null ? 
                    vente.getDateVente().format(DATE_FORMATTER) : "-",
                "ID Vente", vente.getId().toString()
            ))
            
            // Section 2 : Client
            .section("Client", Map.of(
                "Nom", vente.getClient() != null ? vente.getClient().getNom() : "-",
                "Type", vente.getClient() != null && vente.getClient().getTypeClient() != null ?
                    vente.getClient().getTypeClient().getLibelle() : "-",
                "Contact", vente.getClient() != null && vente.getClient().getContact() != null ?
                    vente.getClient().getContact() : "-",
                "Email", vente.getClient() != null && vente.getClient().getEmail() != null ?
                    vente.getClient().getEmail() : "-"
            ))
            
            // Section 3 :  de la vente
            .section("Détails de la vente", Map.of(
                "Récolte", vente.getRecolte() != null ? 
                    vente.getRecolte().getId().toString() : "-",
                "Lot", vente.getRecolte() != null && vente.getRecolte().getLot() != null ? 
                    vente.getRecolte().getLot().getCode() : "-",
                "Poids vendu", vente.getPoidsVendu() != null ? 
                    vente.getPoidsVendu() + " kg" : "-",
                "Effectif", vente.getEffectifVendu() != null ? 
                    vente.getEffectifVendu() + " unités" : "-",
                "Prix unitaire", vente.getPrixUnitaire() != null ? 
                    String.format("%,.2f", vente.getPrixUnitaire()) + " MGA/kg" : "-",
                "Observation", vente.getObservation() != null ? 
                    vente.getObservation() : "-"
            ))
            
            // Total
            .total("Montant total", vente.getMontantTotal() != null ? 
                String.format("%,.2f", vente.getMontantTotal()) + " MGA" : "-");

        byte[] pdf = pdfService.genererFiche(data);
        return PdfResponses.attachment(pdf, "facture_vente_" + vente.getId() + ".pdf");
    }
}