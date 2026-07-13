package mg.itu.aquanova.import_excel.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.import_excel.models.TypeImport;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

/**
 * Produit le modèle de fichier à remplir.
 *
 * C'est la pièce qui justifie Excel plutôt qu'un CSV : le modèle embarque des listes
 * déroulantes alimentées par les données réelles de l'application (codes des lots actifs,
 * noms des aliments). L'utilisateur choisit dans une liste au lieu de recopier un code à
 * la main — et la moitié des erreurs d'import disparaît avant même l'envoi du fichier.
 */
@Service
public class ModeleImportService {

    private static final int LIGNES_A_VALIDER = 500;
    private static final String ONGLET_REFERENCES = "Références";

    private final LotRepository lotRepository;
    private final AlimentRepository alimentRepository;

    public ModeleImportService(LotRepository lotRepository, AlimentRepository alimentRepository) {
        this.lotRepository = lotRepository;
        this.alimentRepository = alimentRepository;
    }

    public byte[] genererModele(TypeImport type) {
        try (XSSFWorkbook classeur = new XSSFWorkbook();
             ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {

            Sheet onglet = classeur.createSheet(type.getLibelle());
            CellStyle styleEntete = styleGras(classeur);

            Row entete = onglet.createRow(0);
            List<String> colonnes = type.getColonnes();
            for (int c = 0; c < colonnes.size(); c++) {
                entete.createCell(c).setCellValue(colonnes.get(c));
                entete.getCell(c).setCellStyle(styleEntete);
            }

            // Ligne d'exemple : elle montre le format attendu, notamment celui de la date.
            Row exemple = onglet.createRow(1);
            List<String> valeurs = type.getExemple();
            for (int c = 0; c < valeurs.size(); c++) {
                exemple.createCell(c).setCellValue(valeurs.get(c));
            }

            // Onglet de références, masqué : il alimente les listes déroulantes.
            Sheet references = classeur.createSheet(ONGLET_REFERENCES);
            List<String> codesLots = codesLotsActifs();
            List<String> nomsAliments = nomsAliments();
            ecrireColonne(references, 0, "Lots", codesLots);
            ecrireColonne(references, 1, "Aliments", nomsAliments);
            classeur.setSheetHidden(classeur.getSheetIndex(references), true);

            ajouterListe(onglet, type.getIndexColonneLot(), "A", codesLots.size());
            if (type.getIndexColonneAliment() >= 0) {
                ajouterListe(onglet, type.getIndexColonneAliment(), "B", nomsAliments.size());
            }

            for (int c = 0; c < colonnes.size(); c++) {
                onglet.autoSizeColumn(c);
            }
            onglet.createFreezePane(0, 1);

            classeur.write(sortie);
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la génération du modèle : " + e.getMessage(), e);
        }
    }

    /** Seuls les lots actifs sont proposés : on ne peut pas ajouter d'événement à un lot clôturé. */
    private List<String> codesLotsActifs() {
        return lotRepository.findAll().stream()
                .filter(this::estActif)
                .map(LotModels::getCode)
                .filter(code -> code != null && !code.isBlank())
                .sorted()
                .toList();
    }

    private boolean estActif(LotModels lot) {
        return lot.getStatutLot() == null
                || (lot.getStatutLot().getLibelle() != StatutLotEnum.CLOTURE
                        && lot.getStatutLot().getLibelle() != StatutLotEnum.ANNULE);
    }

    private List<String> nomsAliments() {
        return alimentRepository.findAll().stream()
                .map(Aliment::getNom)
                .filter(nom -> nom != null && !nom.isBlank())
                .sorted()
                .toList();
    }

    private void ecrireColonne(Sheet feuille, int colonne, String titre, List<String> valeurs) {
        Row entete = feuille.getRow(0) != null ? feuille.getRow(0) : feuille.createRow(0);
        entete.createCell(colonne).setCellValue(titre);

        for (int i = 0; i < valeurs.size(); i++) {
            Row ligne = feuille.getRow(i + 1) != null ? feuille.getRow(i + 1) : feuille.createRow(i + 1);
            ligne.createCell(colonne).setCellValue(valeurs.get(i));
        }
    }

    private void ajouterListe(Sheet onglet, int colonne, String colonneReference, int nbValeurs) {
        if (nbValeurs == 0) {
            return;   // rien à proposer : une liste vide bloquerait la saisie
        }
        DataValidationHelper aide = onglet.getDataValidationHelper();
        String plage = String.format("'%s'!$%s$2:$%s$%d",
                ONGLET_REFERENCES, colonneReference, colonneReference, nbValeurs + 1);

        DataValidationConstraint contrainte = aide.createFormulaListConstraint(plage);
        CellRangeAddressList cellules = new CellRangeAddressList(1, LIGNES_A_VALIDER, colonne, colonne);

        DataValidation validation = aide.createValidation(contrainte, cellules);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("Valeur invalide", "Choisissez une valeur dans la liste déroulante.");
        onglet.addValidationData(validation);
    }

    private CellStyle styleGras(Workbook classeur) {
        Font police = classeur.createFont();
        police.setBold(true);
        CellStyle style = classeur.createCellStyle();
        style.setFont(police);
        return style;
    }
}
