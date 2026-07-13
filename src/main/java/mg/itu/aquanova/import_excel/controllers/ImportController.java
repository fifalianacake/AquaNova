package mg.itu.aquanova.import_excel.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.itu.aquanova.export_excel.models.ExcelResponses;
import mg.itu.aquanova.export_excel.models.FeuilleExcel;
import mg.itu.aquanova.export_excel.services.ExcelExportService;
import mg.itu.aquanova.import_excel.models.ApercuImport;
import mg.itu.aquanova.import_excel.models.LigneImport;
import mg.itu.aquanova.import_excel.models.TypeImport;
import mg.itu.aquanova.import_excel.services.ImportDonneesService;
import mg.itu.aquanova.import_excel.services.ModeleImportService;

@Controller
@RequestMapping("/imports")
public class ImportController {

    private static final String CLE_APERCU = "apercuImport";

    private final ImportDonneesService importDonneesService;
    private final ModeleImportService modeleImportService;
    private final ExcelExportService excelExportService;

    public ImportController(ImportDonneesService importDonneesService,
                            ModeleImportService modeleImportService,
                            ExcelExportService excelExportService) {
        this.importDonneesService = importDonneesService;
        this.modeleImportService = modeleImportService;
        this.excelExportService = excelExportService;
    }

    @GetMapping
    public String accueil(Model model) {
        model.addAttribute("types", TypeImport.values());
        return "imports/index";
    }

    /** Modèle de fichier à remplir, avec les listes déroulantes des lots et des aliments. */
    @GetMapping("/{slug}/modele")
    public ResponseEntity<byte[]> telechargerModele(@PathVariable String slug) {
        TypeImport type = TypeImport.parSlug(slug);
        byte[] classeur = modeleImportService.genererModele(type);
        return ExcelResponses.attachment(classeur, "modele-" + type.getSlug() + ".xlsx");
    }

    /** Étape 1 : lecture et validation. Rien n'est écrit en base à ce stade. */
    @PostMapping("/{slug}/apercu")
    public String analyser(@PathVariable String slug,
                           @RequestParam("fichier") MultipartFile fichier,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        TypeImport type = TypeImport.parSlug(slug);
        try {
            ApercuImport apercu = importDonneesService.analyser(fichier, type);
            session.setAttribute(CLE_APERCU, apercu);
            model.addAttribute("apercu", apercu);
            return "imports/apercu";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/imports";
        }
    }

    /** Étape 2 : écriture effective, en une seule transaction. */
    @PostMapping("/confirmer")
    public String confirmer(HttpSession session, RedirectAttributes redirectAttributes) {
        ApercuImport apercu = (ApercuImport) session.getAttribute(CLE_APERCU);
        if (apercu == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Aucun import en attente. Déposez à nouveau votre fichier.");
            return "redirect:/imports";
        }

        try {
            int nbLignes = importDonneesService.executer(apercu);
            session.removeAttribute(CLE_APERCU);
            redirectAttributes.addFlashAttribute("success",
                    nbLignes + " ligne(s) importée(s) : " + apercu.getType().getLibelle().toLowerCase() + ".");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/imports";
    }

    @PostMapping("/annuler")
    public String annuler(HttpSession session) {
        session.removeAttribute(CLE_APERCU);
        return "redirect:/imports";
    }

    /**
     * Rapport de rejet : les lignes refusées, telles quelles, avec la raison en dernière
     * colonne. L'utilisateur corrige ce fichier et le réimporte directement.
     */
    @GetMapping("/rejets")
    public ResponseEntity<byte[]> telechargerRejets(HttpSession session) {
        ApercuImport apercu = (ApercuImport) session.getAttribute(CLE_APERCU);
        if (apercu == null || apercu.getLignesRejetees().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<String> colonnes = new ArrayList<>(apercu.getType().getColonnes());
        colonnes.add("Motif du rejet");

        FeuilleExcel feuille = FeuilleExcel.of("Lignes rejetées")
                .titre("Lignes rejetées — " + apercu.getType().getLibelle())
                .sousTitre("Fichier d'origine : " + apercu.getNomFichier()
                        + ". Corrigez la dernière colonne, supprimez-la, puis réimportez ce fichier.")
                .colonnes(colonnes.toArray(new String[0]));

        for (LigneImport ligne : apercu.getLignesRejetees()) {
            List<Object> valeurs = new ArrayList<>(ligne.getCellules());
            valeurs.add(ligne.getErreur());
            feuille.ligne(valeurs.toArray());
        }

        byte[] classeur = excelExportService.genererClasseur(feuille);
        return ExcelResponses.attachment(classeur, "rejets-" + apercu.getType().getSlug() + ".xlsx");
    }
}
