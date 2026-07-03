package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import mg.itu.aquanova.achat.dto.AchatAlevinForm;
import mg.itu.aquanova.achat.dto.AchatIntrantForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.Intrant;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;
import mg.itu.aquanova.achat.repositories.IntrantRepository;
import mg.itu.aquanova.achat.repositories.MouvementStockIntrantRepository;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.referentiel.models.Bassin;
import mg.itu.aquanova.referentiel.models.EspecesModels;
import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
import mg.itu.aquanova.referentiel.repositories.BassinsRepository;
import mg.itu.aquanova.referentiel.repositories.EspecesRepository;
import mg.itu.aquanova.referentiel.services.BassinService;

@Service
public class AchatAlevinService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final LotRepository lotRepository;
    private final CategorieDepenseService categorieDepenseService;
    private final EspecesRepository especesRepository;
    private final MouvementStockIntrantRepository mouvementRepository;
    private final BassinsRepository bassinsRepository;
    private final LotService lotService;

    public AchatAlevinService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            LotRepository lotRepository,
            CategorieDepenseService categorieDepenseService,
            EspecesRepository especesRepository,
            MouvementStockIntrantRepository mouvementRepository,
            BassinsRepository bassinsRepository,
            LotService lotService) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.lotRepository = lotRepository;
        this.categorieDepenseService = categorieDepenseService;
        this.especesRepository = especesRepository;
        this.mouvementRepository = mouvementRepository;
        this.bassinsRepository = bassinsRepository;
        this.lotService = lotService;
    }

    @Transactional
    public Achat creerAchatAlevin(AchatAlevinForm form) {
        validerFormulaire(form);

        Fournisseur fournisseur = fournisseurRepository.findById(form.getFournisseurId())
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + form.getFournisseurId()));
        CategorieDepense categorie = categorieDepenseRepository.findById(form.getCategorieDepenseId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie de dépense introuvable : " + form.getCategorieDepenseId()));
        EspecesModels especes = especesRepository.findById(form.getEspeceId())
                .orElseThrow(() -> new EntityNotFoundException("Espece introuvable : " + form.getEspeceId()));

        if (!categorieDepenseService.estCategorieAchatAlevin(categorie)) {
            throw new IllegalArgumentException("La catégorie doit être ACHAT_ALEVINS.");
        }

        BigDecimal montantLigne = BigDecimal.valueOf(form.getEffectif()).multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(blankToNull(form.getReferenceFacture()));
        achat.setObservation(blankToNull(null));
        achat.setStatutAchat(StatutAchat.VALIDE);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(especes.getNom());
        ligne.setEspece(especes);
        ligne.setQuantite(BigDecimal.valueOf(form.getEffectif()));
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);

        LotModels lot = new LotModels();
        lot.setEspece(especes);
        lot.setCode(String.format("LOT-%03d", this.lotRepository.count()));

        Optional<Bassin> bassin = this.bassinsRepository.findFirstByStatutNomIgnoreCase("LIBRE");
        if(!bassin.isPresent()) {
            throw new IllegalArgumentException("Aucun bassin n'est libre pour effectuer cette achat d'Alevin");
        }

        lot.setBassin(bassin.orElse(null));
        lot.setStadeCroissance(null);
        lot.setStatutLot(null);
        lot.setDateMiseEnCharge(LocalDate.now());
        lot.setEffectifInitial(form.getEffectif());
        lot.setEffectifActuel(form.getEffectif());
        lot.setPoidsMoyenInitial(form.getPoidsMoyen());
        lot.setPoidsMoyenActuel(form.getPoidsMoyen());

        this.lotService.validerLot(lot, null);

        ligne.setLot(lot);
        achat.addLigne(ligne);

        Achat sauvegarde = achatRepository.save(achat);

        return sauvegarde;
    }

    private void validerFormulaire(AchatAlevinForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Le formulaire d'achat est obligatoire.");
        }
        if (form.getDateAchat() == null) {
            throw new IllegalArgumentException("La date d'achat est obligatoire.");
        }
        if (form.getFournisseurId() == null) {
            throw new IllegalArgumentException("Le fournisseur est obligatoire.");
        }
        if (form.getCategorieDepenseId() == null) {
            throw new IllegalArgumentException("La catégorie de dépense est obligatoire.");
        }
        if (form.getEspeceId() == null) {
            throw new IllegalArgumentException("L'espece est obligatoire.");
        }
        if (form.getEffectif() == null || form.getEffectif().compareTo(0) <= 0) {
            throw new IllegalArgumentException("L'effectif doit être strictement positive.");
        }
        if (form.getPoidsMoyen() == null || form.getPoidsMoyen().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le poids moyen doit exister.");
        }
        if(form.getPrixUnitaire() == null || form.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit exister.");
        }
        if(form.getMontantTotal() == null || form.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total doit exister.");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

}
