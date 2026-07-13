package mg.itu.aquanova.import_excel.services;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import mg.itu.aquanova.import_excel.models.LigneImport;
import mg.itu.aquanova.import_excel.models.TypeImport;

@Service
public class LecteurExcelService {

    private static final int LIGNES_MAX = 5000;

    public List<LigneImport> lire(MultipartFile fichier, TypeImport type) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier n'a été fourni.");
        }
        String nom = fichier.getOriginalFilename();
        if (nom == null || !nom.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException(
                    "Le fichier doit être au format Excel (.xlsx). Les fichiers .xls et .csv ne sont pas acceptés.");
        }

        List<LigneImport> lignes = new ArrayList<>();

        try (InputStream flux = fichier.getInputStream();
             Workbook classeur = new XSSFWorkbook(flux)) {

            Sheet onglet = classeur.getSheetAt(0);
            int nbColonnes = type.getColonnes().size();

            for (Row row : onglet) {
                // La première ligne est l'en-tête du modèle : on la saute.
                if (row.getRowNum() == 0) {
                    continue;
                }
                if (lignes.size() >= LIGNES_MAX) {
                    throw new IllegalArgumentException(
                            "Le fichier dépasse " + LIGNES_MAX + " lignes. Découpez-le en plusieurs fichiers.");
                }

                List<String> cellules = new ArrayList<>();
                for (int c = 0; c < nbColonnes; c++) {
                    cellules.add(lireCellule(row.getCell(c)));
                }
                if (cellules.stream().allMatch(v -> v == null || v.isBlank())) {
                    continue;   // ligne vide : on l'ignore silencieusement
                }
                lignes.add(new LigneImport(row.getRowNum() + 1, cellules));
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Le fichier n'a pas pu être lu. Vérifiez qu'il s'agit bien d'un classeur Excel valide.", e);
        }

        if (lignes.isEmpty()) {
            throw new IllegalArgumentException("Le fichier ne contient aucune ligne de données.");
        }
        return lignes;
    }

    private String lireCellule(Cell cellule) {
        if (cellule == null) {
            return null;
        }
        CellType type = cellule.getCellType() == CellType.FORMULA
                ? cellule.getCachedFormulaResultType()
                : cellule.getCellType();

        return switch (type) {
            case STRING -> cellule.getStringCellValue().trim();
            case BOOLEAN -> String.valueOf(cellule.getBooleanCellValue());
            case NUMERIC -> lireNumerique(cellule);
            default -> null;
        };
    }

    private String lireNumerique(Cell cellule) {
        if (DateUtil.isCellDateFormatted(cellule)) {
            return cellule.getLocalDateTimeCellValue().toLocalDate().toString();   // yyyy-MM-dd
        }
        // stripTrailingZeros évite qu'un entier saisi « 20 » ressorte en « 20.0 »
        return BigDecimal.valueOf(cellule.getNumericCellValue())
                .stripTrailingZeros()
                .toPlainString();
    }
}
