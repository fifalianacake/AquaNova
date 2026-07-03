package mg.itu.aquanova.vente.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.vente.models.Client;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.ClientService;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/clients")
public class ClientExportController {

    private final ClientService clientService;
    private final VenteService venteService;
    private final PdfExportService pdfService;

    public ClientExportController(ClientService clientService, 
                                  VenteService venteService,
                                  PdfExportService pdfService) {
        this.clientService = clientService;
        this.venteService = venteService;
        this.pdfService = pdfService;
    }

   
    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exporterFicheClient(@PathVariable Long id) {
        Client client = clientService.trouverParId(id);
        List<Vente> ventes = venteService.getByClient(id);

        // Calcul des statistiques
        int nbVentes = ventes.size();
        double totalPoids = ventes.stream()
                .filter(v -> v.getStatutVente() != null && 
                            !v.getStatutVente().getCode().name().equals("ANNULEE"))
                .mapToDouble(Vente::getPoidsVendu)
                .sum();
        
        double totalMontant = ventes.stream()
                .filter(v -> v.getStatutVente() != null && 
                            !v.getStatutVente().getCode().name().equals("ANNULEE"))
                .mapToDouble(Vente::getMontantTotal)
                .sum();
        
        double panierMoyen = nbVentes > 0 ? totalMontant / nbVentes : 0;
        double prixMoyenKg = totalPoids > 0 ? totalMontant / totalPoids : 0;

        FichePdfData data = FichePdfData.of("FICHE CLIENT")
            .sousTitre(client.getActif() ? "Client actif" : "Client inactif")
            
            // Section 1 : Informations client
            .section("Informations client", Map.of(
                "ID", client.getId().toString(),
                "Nom", client.getNom(),
                "Type", client.getTypeClient() != null ? 
                    client.getTypeClient().getLibelle() : "-",
                "Contact", client.getContact() != null ? client.getContact() : "-",
                "Email", client.getEmail() != null ? client.getEmail() : "-",
                "Adresse", client.getAdresse() != null ? client.getAdresse() : "-",
                "Observation", client.getObservation() != null ? client.getObservation() : "-"
            ))
            
            // Section 2 : Statistiques d'achat
            .section("Statistiques d'achat", Map.of(
                "Nombre total d'achats", String.valueOf(nbVentes),
                "Poids total acheté", String.format("%.2f kg", totalPoids),
                "Chiffre d'affaires total", String.format("%,.2f MGA", totalMontant),
                "Panier moyen", String.format("%,.2f MGA", panierMoyen),
                "Prix moyen au kg", String.format("%,.2f MGA/kg", prixMoyenKg)
            ));

        byte[] pdf = pdfService.genererFiche(data);
        return PdfResponses.attachment(pdf, "client_" + client.getId() + "_" + 
                client.getNom().replace(" ", "_") + ".pdf");
    }
}