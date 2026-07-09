package mg.itu.aquanova.vente.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.itu.aquanova.export_pdf.models.FichePdfData;
import mg.itu.aquanova.export_pdf.models.ListePdfData;
import mg.itu.aquanova.export_pdf.services.PdfExportService;
import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;

@RestController
@RequestMapping("/ventes")
public class HistoriqueExportController {

    private final VenteService venteService;
    private final PdfExportService pdfService;

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistoriqueExportController(VenteService venteService, PdfExportService pdfService) {
        this.venteService = venteService;
        this.pdfService = pdfService;
    }

    
    @GetMapping("/historique/export-pdf")
    public ResponseEntity<byte[]> exporterHistorique(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String clientNom) {

        
        List<Vente> ventes = venteService.search(
                null,          
                clientNom,      
                null,          
                null,           
                dateDebut,      
                dateFin,        
                null            
        );

        // Construire les lignes
        List<List<String>> lignes = new ArrayList<>();
        double totalPoids = 0;
        double totalMontant = 0;
        int nbVentesNonAnnulees = 0;

        for (Vente v : ventes) {
            // Ignorer les ventes annuldes pour les totaux
            boolean isAnnulee = v.getStatutVente() != null && 
                    StatutVenteEnum.ANNULEE == v.getStatutVente().getCode();
            
            if (!isAnnulee) {
                totalPoids += v.getPoidsVendu() != null ? v.getPoidsVendu() : 0;
                totalMontant += v.getMontantTotal() != null ? v.getMontantTotal() : 0;
                nbVentesNonAnnulees++;
            }

            List<String> ligne = new ArrayList<>();
            ligne.add(v.getId().toString());
            ligne.add(v.getDateVente() != null ? 
                    v.getDateVente().format(DATE_FORMATTER) : "-");
            ligne.add(v.getClient() != null ? v.getClient().getNom() : "-");
            ligne.add(v.getRecolte() != null && v.getRecolte().getLot() != null ? 
                    v.getRecolte().getLot().getCode() : "-");
            ligne.add(v.getPoidsVendu() != null ? 
                    String.format("%.2f", v.getPoidsVendu()) : "-");
            ligne.add(v.getPrixUnitaire() != null ? 
                    String.format("%.2f", v.getPrixUnitaire()) : "-");
            ligne.add(v.getMontantTotal() != null ? 
                    String.format("%.2f", v.getMontantTotal()) : "-");
            ligne.add(v.getStatutVente() != null ? 
                    v.getStatutVente().getCode().name() : "-");
            lignes.add(ligne);
        }

        // Construction du DTO
        ListePdfData data = ListePdfData.of("HISTORIQUE COMMERCIAL DES VENTES")
            .filtre("Date début", dateDebut != null ? dateDebut.toString() : null)
            .filtre("Date fin", dateFin != null ? dateFin.toString() : null)
            .filtre("Client ID", clientId)
            .filtre("Client", clientNom)
            .filtre("Statut", statut)
            .colonnes(List.of("N°", "Date", "Client", "Lot", "Poids (kg)", 
                    "Prix Unitaire", "Montant (MGA)", "Statut"))
            .lignes(lignes)
            .total("Total CA", String.format("%,.2f MGA", totalMontant))
            .total("Total Poids", String.format("%.2f kg", totalPoids))
            .total("Nombre de ventes", String.valueOf(nbVentesNonAnnulees));

        byte[] pdf = pdfService.genererListe(data);
        return PdfResponses.attachment(pdf, "historique_ventes.pdf");
    }
}
