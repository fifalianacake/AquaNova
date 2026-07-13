package mg.itu.aquanova.export_excel.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import mg.itu.aquanova.achat.dto.DepenseFilter;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.Depense;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.services.AchatService;
import mg.itu.aquanova.achat.services.DepenseService;
import mg.itu.aquanova.export_excel.models.ExcelResponses;
import mg.itu.aquanova.export_excel.models.FeuilleExcel;
import mg.itu.aquanova.export_excel.services.ExcelExportService;
import mg.itu.aquanova.finance.dto.RentabiliteLotDTO;
import mg.itu.aquanova.finance.services.RentabiliteLotService;
import mg.itu.aquanova.production.services.LotFilter;
import mg.itu.aquanova.vente.dto.TransactionFilterDTO;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.services.VenteService;

/**
 * Exports Excel (module 8).
 *
 * Chaque export rejoue les filtres de la page web dont il est issu : le classeur et
 * l'écran affichent nécessairement les mêmes lignes. Les montants, quantités et dates
 * sont écrits comme de vraies valeurs numériques, ce qui permet à l'utilisateur de
 * retravailler les chiffres — c'est la raison d'être de cet export par rapport au PDF.
 */
@Controller
public class ExcelExportController {

    private final VenteService venteService;
    private final AchatService achatService;
    private final DepenseService depenseService;
    private final RentabiliteLotService rentabiliteLotService;
    private final ExcelExportService excelExportService;

    public ExcelExportController(VenteService venteService,
                                 AchatService achatService,
                                 DepenseService depenseService,
                                 RentabiliteLotService rentabiliteLotService,
                                 ExcelExportService excelExportService) {
        this.venteService = venteService;
        this.achatService = achatService;
        this.depenseService = depenseService;
        this.rentabiliteLotService = rentabiliteLotService;
        this.excelExportService = excelExportService;
    }

    // ------------------------------------------------------------------ Ventes
    @GetMapping("/ventes/export-excel")
    public ResponseEntity<byte[]> exporterVentes(@ModelAttribute("filter") TransactionFilterDTO filter) {
        List<Vente> ventes = venteService.listerPourExport(filter);

        FeuilleExcel feuille = FeuilleExcel.of("Ventes")
                .titre("Historique des ventes")
                .sousTitre(periode(filter.getDateDebut(), filter.getDateFin()))
                .colonnes("N°", "Date", "Client", "Lot", "Poids (kg)", "Effectif",
                        "Prix unitaire (Ar/kg)", "Montant (Ar)", "Statut")
                .totaliser(4, 5, 7);

        for (Vente vente : ventes) {
            feuille.ligne(
                    vente.getId(),
                    vente.getDateVente(),
                    vente.getClient() != null ? vente.getClient().getNom() : null,
                    vente.getRecolte() != null && vente.getRecolte().getLot() != null
                            ? vente.getRecolte().getLot().getCode() : null,
                    vente.getPoidsVendu(),
                    vente.getEffectifVendu(),
                    vente.getPrixUnitaire(),
                    montantVente(vente),
                    vente.getStatutVente() != null ? vente.getStatutVente().getCode().name() : null);
        }

        return ExcelResponses.attachment(excelExportService.genererClasseur(feuille), "ventes.xlsx");
    }

    // ------------------------------------------------------------------ Achats
    @GetMapping("/achats/export-excel")
    public ResponseEntity<byte[]> exporterAchats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) StatutAchat statut) {

        List<Achat> achats = achatService.listerPourExport(dateDebut, dateFin, statut);

        FeuilleExcel entetes = FeuilleExcel.of("Achats")
                .titre("Achats")
                .sousTitre(periode(dateDebut, dateFin))
                .colonnes("N°", "Date", "Référence facture", "Fournisseur", "Catégorie",
                        "Montant (Ar)", "Statut")
                .totaliser(5);

        // Le détail des lignes part dans un second onglet : un achat peut en porter plusieurs,
        // et les mélanger avec les en-têtes fausserait le total.
        FeuilleExcel lignes = FeuilleExcel.of("Lignes d'achat")
                .titre("Détail des lignes d'achat")
                .colonnes("Achat n°", "Date", "Désignation", "Article", "Quantité", "Unité",
                        "Prix unitaire (Ar)", "Montant (Ar)", "Lot créé", "Statut de l'achat")
                .totaliser(7);

        for (Achat achat : achats) {
            entetes.ligne(
                    achat.getId(),
                    achat.getDateAchat(),
                    achat.getReferenceFacture(),
                    achat.getFournisseur() != null ? achat.getFournisseur().getNom() : null,
                    achat.getCategorieDepense() != null ? achat.getCategorieDepense().getLibelle() : null,
                    achat.getMontantTotal(),
                    achat.getStatutAchat() != null ? achat.getStatutAchat().name() : null);

            if (achat.getLignes() == null) {
                continue;
            }
            for (LigneAchat ligne : achat.getLignes()) {
                lignes.ligne(
                        achat.getId(),
                        achat.getDateAchat(),
                        ligne.getDesignation(),
                        article(ligne),
                        ligne.getQuantite(),
                        ligne.getUnite(),
                        ligne.getPrixUnitaire(),
                        ligne.getMontantLigne(),
                        ligne.getLot() != null ? ligne.getLot().getCode() : null,
                        achat.getStatutAchat() != null ? achat.getStatutAchat().name() : null);
            }
        }

        byte[] classeur = excelExportService.genererClasseur(List.of(entetes, lignes));
        return ExcelResponses.attachment(classeur, "achats.xlsx");
    }

    // ------------------------------------------------------------------ Dépenses
    @GetMapping("/depenses/export-excel")
    public ResponseEntity<byte[]> exporterDepenses(@ModelAttribute("filter") DepenseFilter filter) {
        List<Depense> depenses = depenseService.listerPourExport(filter);

        FeuilleExcel feuille = FeuilleExcel.of("Dépenses")
                .titre("Historique des dépenses")
                .sousTitre(periode(filter.getDateDebut(), filter.getDateFin()))
                .colonnes("N°", "Date", "Libellé", "Catégorie", "Mode de paiement",
                        "Référence", "Montant (Ar)")
                .totaliser(6);

        for (Depense depense : depenses) {
            feuille.ligne(
                    depense.getId(),
                    depense.getDateDepense(),
                    depense.getLibelle(),
                    depense.getCategorieDepense() != null ? depense.getCategorieDepense().getLibelle() : null,
                    depense.getModePaiement(),
                    depense.getReference(),
                    depense.getMontant());
        }

        return ExcelResponses.attachment(excelExportService.genererClasseur(feuille), "depenses.xlsx");
    }

    // ------------------------------------------------------------------ Rentabilité
    @GetMapping("/finance/lots/export-excel")
    public ResponseEntity<byte[]> exporterRentabilite(@ModelAttribute("lotFilter") LotFilter lotFilter) {
        List<RentabiliteLotDTO> lots = rentabiliteLotService.lister(lotFilter, Pageable.unpaged()).getContent();

        FeuilleExcel feuille = FeuilleExcel.of("Rentabilité")
                .titre("Rentabilité par lot")
                .sousTitre("Marge brute = chiffre d'affaires réalisé − coûts directs "
                        + "(alevins acquis + aliment distribué)")
                .colonnes("Lot", "Espèce", "Bassin", "Statut du lot", "Chiffre d'affaires (Ar)",
                        "Coût des alevins (Ar)", "Coût de l'alimentation (Ar)", "Coûts directs (Ar)",
                        "Marge brute (Ar)", "Taux de marge (%)", "Poids vendu (kg)",
                        "Coût direct par kg (Ar)", "Rentabilité")
                .totaliser(4, 5, 6, 7, 8, 10);

        for (RentabiliteLotDTO lot : lots) {
            feuille.ligne(
                    lot.getLotCode(),
                    lot.getEspece(),
                    lot.getBassin(),
                    lot.getStatutLot(),
                    lot.getChiffreAffaires(),
                    lot.getCoutAlevins(),
                    lot.getCoutAlimentation(),
                    lot.getCoutsDirects(),
                    lot.getMargeBrute(),
                    lot.getTauxMargeBrute(),
                    lot.getPoidsVendu(),
                    lot.getCoutDirectParKgVendu(),
                    lot.getStatutRentabilite() != null ? lot.getStatutRentabilite().name() : null);
        }

        return ExcelResponses.attachment(excelExportService.genererClasseur(feuille), "rentabilite-lots.xlsx");
    }

    // ------------------------------------------------------------------ Utilitaires
    private String article(LigneAchat ligne) {
        if (ligne.getAliment() != null) {
            return ligne.getAliment().getNom();
        }
        return ligne.getEspece() != null ? ligne.getEspece().getNom() : null;
    }

    private BigDecimal montantVente(Vente vente) {
        boolean annulee = vente.getStatutVente() != null
                && StatutVenteEnum.ANNULEE == vente.getStatutVente().getCode();
        if (annulee || vente.getMontantTotal() == null) {
            return null;
        }
        return BigDecimal.valueOf(vente.getMontantTotal());
    }

    private String periode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null && dateFin == null) {
            return "Toutes périodes confondues";
        }
        return "Période : " + (dateDebut != null ? dateDebut : "origine")
                + " au " + (dateFin != null ? dateFin : "aujourd'hui");
    }
}
