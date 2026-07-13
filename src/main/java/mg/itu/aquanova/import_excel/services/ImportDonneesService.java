package mg.itu.aquanova.import_excel.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import mg.itu.aquanova.alimentation.dto.DistributionDTO;
import mg.itu.aquanova.alimentation.services.DistributionService;
import mg.itu.aquanova.import_excel.models.ApercuImport;
import mg.itu.aquanova.import_excel.models.LigneImport;
import mg.itu.aquanova.import_excel.models.TypeImport;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.MortaliteModels;
import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.services.MortaliteService;
import mg.itu.aquanova.production.services.PeseeService;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class ImportDonneesService {

    private static final DateTimeFormatter FORMAT_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LecteurExcelService lecteurExcelService;
    private final LotRepository lotRepository;
    private final AlimentRepository alimentRepository;
    private final PeseeService peseeService;
    private final MortaliteService mortaliteService;
    private final DistributionService distributionService;

    public ImportDonneesService(LecteurExcelService lecteurExcelService,
                                LotRepository lotRepository,
                                AlimentRepository alimentRepository,
                                PeseeService peseeService,
                                MortaliteService mortaliteService,
                                DistributionService distributionService) {
        this.lecteurExcelService = lecteurExcelService;
        this.lotRepository = lotRepository;
        this.alimentRepository = alimentRepository;
        this.peseeService = peseeService;
        this.mortaliteService = mortaliteService;
        this.distributionService = distributionService;
    }

    // ------------------------------------------------------------------ Aperçu
    public ApercuImport analyser(MultipartFile fichier, TypeImport type) {
        List<LigneImport> lignes = lecteurExcelService.lire(fichier, type);

        ApercuImport apercu = new ApercuImport(type, fichier.getOriginalFilename());
        for (LigneImport ligne : lignes) {
            ligne.setErreur(valider(ligne, type));
            apercu.ajouter(ligne);
        }
        return apercu;
    }

    /** @return le message d'erreur, ou null si la ligne est valide. */
    private String valider(LigneImport ligne, TypeImport type) {
        LotModels lot = trouverLot(ligne.cellule(0));
        if (lot == null) {
            return "Aucun lot ne porte le code « " + valeurOuVide(ligne.cellule(0)) + " ».";
        }
        if (estCloture(lot)) {
            return "Le lot " + lot.getCode() + " est clôturé ou annulé : aucun événement ne peut y être ajouté.";
        }

        LocalDate date = lireDate(ligne.cellule(1));
        if (date == null) {
            return "Date illisible ou absente : utilisez le format jj/mm/aaaa.";
        }
        if (date.isAfter(LocalDate.now())) {
            return "La date est dans le futur.";
        }
        if (lot.getDateMiseEnCharge() != null && date.isBefore(lot.getDateMiseEnCharge())) {
            return "La date est antérieure à la mise en charge du lot (" + lot.getDateMiseEnCharge() + ").";
        }

        return switch (type) {
            case PESEE -> validerPesee(ligne);
            case MORTALITE -> validerMortalite(ligne, lot);
            case DISTRIBUTION -> validerDistribution(ligne);
        };
    }

    private String validerPesee(LigneImport ligne) {
        Integer nbEchantillon = lireEntier(ligne.cellule(2));
        if (nbEchantillon == null || nbEchantillon <= 0) {
            return "Le nombre d'échantillons doit être un entier strictement positif.";
        }
        BigDecimal poidsTotal = lireDecimal(ligne.cellule(3));
        if (poidsTotal == null || poidsTotal.signum() <= 0) {
            return "Le poids total de l'échantillon doit être un nombre strictement positif.";
        }
        return null;
    }

    private String validerMortalite(LigneImport ligne, LotModels lot) {
        Integer nbMorts = lireEntier(ligne.cellule(2));
        if (nbMorts == null || nbMorts <= 0) {
            return "Le nombre de morts doit être un entier strictement positif.";
        }
        if (lot.getEffectifActuel() != null && nbMorts > lot.getEffectifActuel()) {
            return "Le nombre de morts (" + nbMorts + ") dépasse l'effectif actuel du lot ("
                    + lot.getEffectifActuel() + ").";
        }
        return null;
    }

    private String validerDistribution(LigneImport ligne) {
        if (trouverAliment(ligne.cellule(2)) == null) {
            return "Aucun aliment ne porte le nom « " + valeurOuVide(ligne.cellule(2)) + " ».";
        }
        BigDecimal quantite = lireDecimal(ligne.cellule(3));
        if (quantite == null || quantite.signum() <= 0) {
            return "La quantité doit être un nombre strictement positif.";
        }
        return null;
    }

    // ------------------------------------------------------------------ Écriture
    /**
     * Écrit les lignes valides. Toute exception remontée par un service métier annule
     * l'intégralité de l'import (rollback), et le message est renvoyé à l'utilisateur.
     */
    @Transactional
    public int executer(ApercuImport apercu) {
        if (apercu == null || !apercu.isImportPossible()) {
            throw new IllegalStateException("Aucune ligne valide à importer.");
        }

        for (LigneImport ligne : apercu.getLignesValides()) {
            try {
                ecrire(ligne, apercu.getType());
            } catch (RuntimeException e) {
                // On enrichit le message avec le numéro de ligne : sans lui, l'utilisateur
                // est incapable de savoir quelle ligne de son fichier corriger.
                throw new IllegalStateException(
                        "Import annulé : la ligne " + ligne.getNumeroLigne() + " a été refusée ("
                                + e.getMessage() + "). Aucune donnée n'a été enregistrée.", e);
            }
        }
        return apercu.getLignesValides().size();
    }

    private void ecrire(LigneImport ligne, TypeImport type) {
        LotModels lot = trouverLot(ligne.cellule(0));
        LocalDate date = lireDate(ligne.cellule(1));

        switch (type) {
            case PESEE -> peseeService.enregistrerPesee(
                    lot.getId(),
                    date,
                    lireEntier(ligne.cellule(2)),
                    lireDecimal(ligne.cellule(3)).doubleValue(),
                    ligne.cellule(4));

            case MORTALITE -> {
                MortaliteModels mortalite = new MortaliteModels();
                mortalite.setLot(lot);
                mortalite.setDateMortalite(date);
                mortalite.setNbMorts(lireEntier(ligne.cellule(2)));
                mortalite.setCause(ligne.cellule(3));
                mortaliteService.save(mortalite);
            }

            case DISTRIBUTION -> {
                Aliment aliment = trouverAliment(ligne.cellule(2));
                DistributionDTO dto = new DistributionDTO();
                dto.setDateDistribution(date);
                dto.setIdLot(lot.getId());
                dto.setIdAliment(aliment.getId());
                dto.setQuantite(lireDecimal(ligne.cellule(3)));
                distributionService.saveDistribution(dto);
            }
        }
    }

    // ------------------------------------------------------------------ Conversions
    private LotModels trouverLot(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return lotRepository.findByCode(code.trim()).orElse(null);
    }

    private Aliment trouverAliment(String nom) {
        if (nom == null || nom.isBlank()) {
            return null;
        }
        Optional<Aliment> aliment = alimentRepository.findAll().stream()
                .filter(a -> a.getNom() != null && a.getNom().equalsIgnoreCase(nom.trim()))
                .findFirst();
        return aliment.orElse(null);
    }

    private boolean estCloture(LotModels lot) {
        return lot.getStatutLot() != null
                && (lot.getStatutLot().getLibelle() == StatutLotEnum.CLOTURE
                        || lot.getStatutLot().getLibelle() == StatutLotEnum.ANNULE);
    }

    /** Accepte le format saisi à la main (jj/mm/aaaa) et celui d'une vraie cellule date Excel. */
    private LocalDate lireDate(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String texte = valeur.trim();
        try {
            return LocalDate.parse(texte, FORMAT_FR);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(texte);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }

    private Integer lireEntier(String valeur) {
        BigDecimal nombre = lireDecimal(valeur);
        if (nombre == null || nombre.stripTrailingZeros().scale() > 0) {
            return null;
        }
        return nombre.intValue();
    }

    private BigDecimal lireDecimal(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            // On tolère la virgule décimale, réflexe naturel d'un utilisateur francophone.
            return new BigDecimal(valeur.trim().replace(",", ".").replace(" ", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valeurOuVide(String valeur) {
        return valeur == null ? "" : valeur;
    }
}
