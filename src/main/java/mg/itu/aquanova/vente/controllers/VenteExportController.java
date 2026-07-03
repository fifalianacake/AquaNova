package mg.itu.aquanova.vente.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;
import mg.itu.aquanova.export_pdf.services.GenericFichePdfService;


@RestController
@RequestMapping("/ventes")
public class VenteExportController {

    private final VenteService venteService;
    private final GenericFichePdfService pdfService;

    public VenteExportController(VenteService venteService, GenericFichePdfService pdfService) {
        this.venteService = venteService;
        this.pdfService = pdfService;
    }

    @GetMapping("/{id}/export-pdf")
    public void exporter(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Vente vente = venteService.trouverParId(id);

        // Construire les données : liste de [label, valeur]
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"ID Vente", vente.getId().toString()});
        data.add(new String[]{"Date vente", vente.getDateVente().toString()});
        data.add(new String[]{"Client", vente.getClient().getNom()});
        data.add(new String[]{"Type Client", vente.getClient().getTypeClient().getLibelle()});
        data.add(new String[]{"Contact", vente.getClient().getContact() != null ? vente.getClient().getContact() : "-"});
        data.add(new String[]{"Récolte", vente.getRecolte().getId().toString()});
        
        String lot = vente.getRecolte().getLot() != null ? vente.getRecolte().getLot().getCodeLot() : "-";
        data.add(new String[]{"Lot", lot});
        
        data.add(new String[]{"Poids vendu", vente.getPoidsVendu() + " kg"});
        
        String effectif = vente.getEffectifVendu() != null ? vente.getEffectifVendu() + " unités" : "-";
        data.add(new String[]{"Effectif", effectif});
        
        data.add(new String[]{"Prix unitaire", vente.getPrixUnitaire() + " MGA/kg"});
        data.add(new String[]{"Montant total", vente.getMontantTotal() + " MGA"});
        data.add(new String[]{"Statut", vente.getStatutVente().getCode().name()});
        data.add(new String[]{"Observation", vente.getObservation() != null ? vente.getObservation() : "-"});

        pdfService.export(response, "facture_vente_" + vente.getId() + ".pdf", 
                         "FACTURE DE VENTE N° " + vente.getId(), data);
    }
}