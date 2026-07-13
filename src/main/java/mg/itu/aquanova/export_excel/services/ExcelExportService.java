package mg.itu.aquanova.export_excel.services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.export_excel.models.FeuilleExcel;

@Service
public class ExcelExportService {

    public byte[] genererClasseur(List<FeuilleExcel> feuilles) {
        try (XSSFWorkbook classeur = new XSSFWorkbook();
             ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {

            CellStyle styleEntete = styleGras(classeur);
            CellStyle styleDate = styleDate(classeur, "dd/mm/yyyy");
            CellStyle styleDateHeure = styleDate(classeur, "dd/mm/yyyy hh:mm");

            for (FeuilleExcel feuille : feuilles) {
                ecrireFeuille(classeur, feuille, styleEntete, styleDate, styleDateHeure);
            }

            classeur.write(sortie);
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la génération du classeur Excel : " + e.getMessage(), e);
        }
    }

    public byte[] genererClasseur(FeuilleExcel feuille) {
        return genererClasseur(List.of(feuille));
    }

    private void ecrireFeuille(Workbook classeur, FeuilleExcel feuille,
                               CellStyle styleEntete, CellStyle styleDate, CellStyle styleDateHeure) {
        Sheet onglet = classeur.createSheet(feuille.getNom());
        int ligneCourante = 0;

        // Titre et rappel des filtres : l'export doit rester compréhensible hors contexte.
        if (feuille.getTitre() != null) {
            onglet.createRow(ligneCourante++).createCell(0).setCellValue(feuille.getTitre());
        }
        if (feuille.getSousTitre() != null && !feuille.getSousTitre().isBlank()) {
            onglet.createRow(ligneCourante++).createCell(0).setCellValue(feuille.getSousTitre());
        }
        if (ligneCourante > 0) {
            ligneCourante++;
        }

        int ligneEntete = ligneCourante;
        Row entete = onglet.createRow(ligneCourante++);
        List<String> colonnes = feuille.getColonnes();
        for (int c = 0; c < colonnes.size(); c++) {
            Cell cellule = entete.createCell(c);
            cellule.setCellValue(colonnes.get(c));
            cellule.setCellStyle(styleEntete);
        }

        for (List<Object> ligne : feuille.getLignes()) {
            Row row = onglet.createRow(ligneCourante++);
            for (int c = 0; c < ligne.size(); c++) {
                ecrireCellule(row.createCell(c), ligne.get(c), styleDate, styleDateHeure);
            }
        }

        // Ligne de totaux : une vraie formule SUM, pas une valeur figée, pour que le total
        // suive si l'utilisateur filtre ou supprime des lignes.
        if (!feuille.getColonnesTotalisees().isEmpty() && !feuille.getLignes().isEmpty()) {
            Row totaux = onglet.createRow(ligneCourante);
            Cell libelle = totaux.createCell(0);
            libelle.setCellValue("Total");
            libelle.setCellStyle(styleEntete);

            int premiere = ligneEntete + 2;   // 1-based, juste après l'en-tête
            int derniere = ligneCourante;     // 1-based
            for (Integer c : feuille.getColonnesTotalisees()) {
                String col = CellReference.convertNumToColString(c);
                Cell cellule = totaux.createCell(c);
                cellule.setCellFormula(String.format("SUM(%s%d:%s%d)", col, premiere, col, derniere));
                cellule.setCellStyle(styleEntete);
            }
        }

        onglet.createFreezePane(0, ligneEntete + 1);
        if (!feuille.getLignes().isEmpty()) {
            onglet.setAutoFilter(new CellRangeAddress(
                    ligneEntete, ligneEntete + feuille.getLignes().size(), 0, colonnes.size() - 1));
        }
        for (int c = 0; c < colonnes.size(); c++) {
            onglet.autoSizeColumn(c);
        }
    }

    private void ecrireCellule(Cell cellule, Object valeur, CellStyle styleDate, CellStyle styleDateHeure) {
        if (valeur == null) {
            cellule.setBlank();
        } else if (valeur instanceof BigDecimal nombre) {
            cellule.setCellValue(nombre.doubleValue());
        } else if (valeur instanceof Number nombre) {
            cellule.setCellValue(nombre.doubleValue());
        } else if (valeur instanceof LocalDate date) {
            cellule.setCellValue(date);
            cellule.setCellStyle(styleDate);
        } else if (valeur instanceof LocalDateTime dateHeure) {
            cellule.setCellValue(dateHeure);
            cellule.setCellStyle(styleDateHeure);
        } else {
            cellule.setCellValue(valeur.toString());
        }
    }

    private CellStyle styleGras(Workbook classeur) {
        Font police = classeur.createFont();
        police.setBold(true);
        CellStyle style = classeur.createCellStyle();
        style.setFont(police);
        return style;
    }

    /** Sans format explicite, Excel afficherait le numéro de série de la date. */
    private CellStyle styleDate(Workbook classeur, String format) {
        CellStyle style = classeur.createCellStyle();
        style.setDataFormat(classeur.createDataFormat().getFormat(format));
        return style;
    }
}
