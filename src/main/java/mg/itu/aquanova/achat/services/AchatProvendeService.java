package mg.itu.aquanova.achat.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mg.itu.aquanova.achat.dto.AchatProvendeForm;
import mg.itu.aquanova.achat.models.Achat;
import mg.itu.aquanova.achat.models.CategorieDepense;
import mg.itu.aquanova.achat.models.Fournisseur;
import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.models.StatutAchat;
import mg.itu.aquanova.achat.repositories.AchatRepository;
import mg.itu.aquanova.achat.repositories.CategorieDepenseRepository;
import mg.itu.aquanova.achat.repositories.FournisseurRepository;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.alimentation.repositories.MouvementStockRepository;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

@Service
public class AchatProvendeService {

    private final AchatRepository achatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieDepenseRepository categorieDepenseRepository;
    private final AlimentRepository alimentRepository;
    private final MouvementStockRepository mouvementRepository;

    public AchatProvendeService(
            AchatRepository achatRepository,
            FournisseurRepository fournisseurRepository,
            CategorieDepenseRepository categorieDepenseRepository,
            AlimentRepository alimentRepository,
            MouvementStockRepository mouvementRepository) {
        this.achatRepository = achatRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieDepenseRepository = categorieDepenseRepository;
        this.alimentRepository = alimentRepository;
        this.mouvementRepository = mouvementRepository;
    }

    public Achat trouverParId(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achat introuvable : " + id));
    }

    @Transactional
    public Achat createAchatProvende(AchatProvendeForm form) {
        if (form == null) throw new IllegalArgumentException("Formulaire obligatoire.");
        if (form.getFournisseurId() == null) throw new IllegalArgumentException("Le fournisseur est obligatoire.");
        if (form.getAlimentId() == null) throw new IllegalArgumentException("L'aliment est obligatoire.");
        if (form.getQuantite() == null || form.getQuantite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive.");
        }
        if (form.getPrixUnitaire() == null || form.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas être négatif.");
        }
        if (form.getCategorieDepenseId() == null) {
            throw new IllegalArgumentException("Catégorie ACHAT_PROVENDE obligatoire.");
        }

        Fournisseur fournisseur = fournisseurRepository.findById(form.getFournisseurId())
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable"));
        CategorieDepense categorie = categorieDepenseRepository.findById(form.getCategorieDepenseId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
        Aliment aliment = alimentRepository.findById(form.getAlimentId())
                .orElseThrow(() -> new EntityNotFoundException("Aliment introuvable"));

        if (!"ACHAT_PROVENDE".equalsIgnoreCase(categorie.getCode())) {
            throw new IllegalArgumentException("La catégorie doit être ACHAT_PROVENDE.");
        }

        BigDecimal montantLigne = form.getQuantite().multiply(form.getPrixUnitaire());

        Achat achat = new Achat();
        achat.setDateAchat(form.getDateAchat() != null ? form.getDateAchat() : LocalDate.now());
        achat.setFournisseur(fournisseur);
        achat.setCategorieDepense(categorie);
        achat.setReferenceFacture(form.getReferenceFacture());
        achat.setObservation(form.getObservation());
        achat.setStatutAchat(StatutAchat.BROUILLON);
        achat.setMontantTotal(montantLigne);

        LigneAchat ligne = new LigneAchat();
        ligne.setDesignation(aliment.getNom());
        ligne.setAliment(aliment);
        ligne.setQuantite(form.getQuantite());
        ligne.setUnite("kg"); // Unité par défaut pour la provende
        ligne.setPrixUnitaire(form.getPrixUnitaire());
        ligne.setMontantLigne(montantLigne);
        ligne.setObservation(form.getObservation());
        achat.addLigne(ligne);

        Achat sauvegarde = achatRepository.save(achat);

        if (form.isValiderDirectement()) {
            return validerAchat(sauvegarde.getId());
        }

        return sauvegarde;
    }

    @Transactional
    public Achat validerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (!"ACHAT_PROVENDE".equalsIgnoreCase(achat.getCategorieDepense().getCode())) {
            throw new IllegalArgumentException("Cet achat n'est pas un achat de provende.");
        }

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            return achat;
        }
        if (achat.getStatutAchat() == StatutAchat.ANNULE) {
            throw new IllegalStateException("Un achat annulé ne peut pas être validé.");
        }

        for (LigneAchat ligne : achat.getLignes()) {
            if (ligne.getAliment() != null) {
                createMouvementStockEntree(achat, ligne);
            }
        }

        achat.setStatutAchat(StatutAchat.VALIDE);
        return achatRepository.save(achat);
    }

    public void createMouvementStockEntree(Achat achat, LigneAchat ligne) {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setDateMouvement(achat.getDateAchat() != null ? achat.getDateAchat() : LocalDate.now());
        mouvement.setAliment(ligne.getAliment());
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setQuantite(ligne.getQuantite().doubleValue());
        mouvement.setCommentaire("Entrée automatique suite à la validation de l'achat provende #" + achat.getId());
        mouvementRepository.save(mouvement);
    }

    @Transactional
    public Achat annulerAchat(Long achatId) {
        Achat achat = trouverParId(achatId);

        if (!"ACHAT_PROVENDE".equalsIgnoreCase(achat.getCategorieDepense().getCode())) {
            throw new IllegalArgumentException("Cet achat n'est pas un achat de provende.");
        }

        if (achat.getStatutAchat() == StatutAchat.VALIDE) {
            // L'achat a déjà été validé, on crée un mouvement inverse (SORTIE)
            for (LigneAchat ligne : achat.getLignes()) {
                if (ligne.getAliment() != null) {
                    MouvementStock mouvement = new MouvementStock();
                    mouvement.setDateMouvement(LocalDate.now());
                    mouvement.setAliment(ligne.getAliment());
                    mouvement.setTypeMouvement(TypeMouvement.SORTIE);
                    mouvement.setQuantite(ligne.getQuantite().doubleValue());
                    mouvement.setCommentaire("Annulation de l'achat provende #" + achat.getId());
                    mouvementRepository.save(mouvement);
                }
            }
        }
        achat.setStatutAchat(StatutAchat.ANNULE);
        return achatRepository.save(achat);
    }
}
