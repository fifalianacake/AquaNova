package mg.itu.aquanova.production.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import mg.itu.aquanova.export_pdf.models.PdfResponses;
import mg.itu.aquanova.export_pdf.services.PdfRenderService;
import mg.itu.aquanova.finance.services.RentabiliteLotService;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.services.JournalLotService;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.production.services.MortaliteService;
import mg.itu.aquanova.production.services.PeseeService;
import mg.itu.aquanova.production.services.PrevisionRecolteService;

@Controller
public class ProductionExportController {

    private final LotService lotService;
    private final PeseeService peseeService;
    private final MortaliteService mortaliteService;
    private final JournalLotService journalLotService;
    private final PrevisionRecolteService previsionRecolteService;
    private final RentabiliteLotService rentabiliteLotService;
    private final PdfRenderService pdfRenderService;

    public ProductionExportController(
            LotService lotService,
            PeseeService peseeService,
            MortaliteService mortaliteService,
            JournalLotService journalLotService,
            PrevisionRecolteService previsionRecolteService,
            RentabiliteLotService rentabiliteLotService,
            PdfRenderService pdfRenderService) {
        this.lotService = lotService;
        this.peseeService = peseeService;
        this.mortaliteService = mortaliteService;
        this.journalLotService = journalLotService;
        this.previsionRecolteService = previsionRecolteService;
        this.rentabiliteLotService = rentabiliteLotService;
        this.pdfRenderService = pdfRenderService;
    }

    @GetMapping("/lots/{id}/export-pdf")
    public ResponseEntity<byte[]> exporterFicheLot(@PathVariable Long id) {
        LotModels lot = lotService.trouverParId(id);

        Map<String, Object> modele = new HashMap<>();
        modele.put("lot", lot);
        modele.put("sousTitre", construireSousTitre(lot));
        modele.put("pesees", peseeService.listerPeseesParLot(id));
        modele.put("mortalites", mortaliteService.findByLot(id));
        modele.put("totalMorts", mortaliteService.getTotalMortsByLot(id));
        modele.put("biomasse", calculerBiomasse(lot));
        modele.put("poidsCible", poidsCible(lot));
        modele.put("progression", calculerProgression(lot));
        modele.put("dateRecolteEstimee", previsionRecolteService.estimerDateRecolte(id));
        modele.put("rentabilite", rentabiliteLotService.construirePourLot(lot));

        byte[] pdf = pdfRenderService.rendre("lot", modele);
        return PdfResponses.attachment(pdf, "lot-" + lot.getCode() + ".pdf");
    }

    @GetMapping("/lots/{id}/journal/export-pdf")
    public ResponseEntity<byte[]> exporterJournalLot(@PathVariable Long id) {
        LotModels lot = lotService.trouverParId(id);

        Map<String, Object> modele = new HashMap<>();
        modele.put("lot", lot);
        modele.put("sousTitre", construireSousTitre(lot));
        modele.put("evenements", journalLotService.obtenirJournalParLot(id));

        byte[] pdf = pdfRenderService.rendre("journal-lot", modele);
        return PdfResponses.attachment(pdf, "journal-" + lot.getCode() + ".pdf");
    }

    private String construireSousTitre(LotModels lot) {
        String espece = lot.getEspece() != null ? lot.getEspece().getNom() : "espèce inconnue";
        String bassin = lot.getBassin() != null ? lot.getBassin().getReference() : "aucun bassin";
        return espece + " — bassin " + bassin;
    }

    private Double poidsCible(LotModels lot) {
        if (lot.getEspece() == null || lot.getEspece().getPoidsCibleMoyen() == null) {
            return null;
        }
        return lot.getEspece().getPoidsCibleMoyen().doubleValue();
    }

    /** Biomasse en kg : effectif actuel × poids moyen actuel (exprimé en grammes). */
    private Double calculerBiomasse(LotModels lot) {
        if (lot.getEffectifActuel() == null || lot.getPoidsMoyenActuel() == null) {
            return null;
        }
        return (lot.getEffectifActuel() * lot.getPoidsMoyenActuel()) / 1000.0;
    }

    private Integer calculerProgression(LotModels lot) {
        Double cible = poidsCible(lot);
        if (cible == null || cible <= 0 || lot.getPoidsMoyenActuel() == null) {
            return null;
        }
        return BigDecimal.valueOf(lot.getPoidsMoyenActuel() / cible * 100)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
