package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.admin.models.ParametreSysteme;
import mg.itu.aquanova.admin.repositories.ParametreSystemeRepository;
import mg.itu.aquanova.alimentation.dto.DistributionDTO;
import mg.itu.aquanova.alimentation.models.Distribution;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.services.PrevisionRecolteService;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DistributionService {

    private final DistributionRepository distributionRepository;
    private final LotRepository lotRepository;
    private final MouvementService mouvementStockService;
    private final AlimentRepository alimentRepository;
    private final ParametreSystemeRepository parametreSystemeRepository;
    private final PrevisionRecolteService previsionRecolteService;

    public DistributionService(DistributionRepository distributionRepository,
            LotRepository lotRepository,
            MouvementService mouvementStockService,
            AlimentRepository alimentRepository,
            ParametreSystemeRepository parametreSystemeRepository,
            PrevisionRecolteService previsionRecolteService) {

        this.distributionRepository = distributionRepository;
        this.lotRepository = lotRepository;
        this.mouvementStockService = mouvementStockService;
        this.alimentRepository = alimentRepository;
        this.parametreSystemeRepository = parametreSystemeRepository;
        this.previsionRecolteService = previsionRecolteService;
    }

    public List<mg.itu.aquanova.alimentation.models.Distribution> getAllDistributions() {
        return distributionRepository.findAll();
    }

    public mg.itu.aquanova.alimentation.models.Distribution getDistributionById(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribution introuvable avec l'ID : " + id));
    }

    public void deleteDistribution(Long id) {
        distributionRepository.deleteById(id);
    }

    @Transactional
    public void saveOrUpdateDistribution(DistributionDTO distributionDTO) {
        Distribution distribution = new Distribution();

        if (distributionDTO.getId() != null) {
            distribution = getDistributionById(distributionDTO.getId());
        }

        LotModels lot = lotRepository.findById(distributionDTO.getIdLot())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lot introuvable avec l'ID : " + distributionDTO.getIdLot()));

        if (distributionDTO.getIdAliment() == null)
            throw new IllegalArgumentException("ID de l'aliment est requis");

        Aliment aliment = alimentRepository.findById(distributionDTO.getIdAliment())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aliment introuvable avec l'ID : " + distributionDTO.getIdAliment()));

        distribution.setDateDistribution(distributionDTO.getDateDistribution());
        distribution.setLot(lot);
        distribution.setAliment(aliment);
        distribution.setQuantite(distributionDTO.getQuantite());

        Double rationTheorique = CalculRationTheoriqueCible(distributionDTO);

        distribution.setRationTheorique(rationTheorique);

        distributionRepository.save(distribution);
    }

    public Distribution saveDistribution(DistributionDTO distributionDTO) {

        Distribution distribution = new Distribution();

        LotModels lot = lotRepository.findById(distributionDTO.getIdLot())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lot introuvable avec l'ID : " + distributionDTO.getIdLot()));

        if (distributionDTO.getIdAliment() == null)
            throw new IllegalArgumentException("ID de l'aliment est requis");

        Aliment aliment = alimentRepository.findById(distributionDTO.getIdAliment())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aliment introuvable avec l'ID : " + distributionDTO.getIdAliment()));

        distribution.setDateDistribution(distributionDTO.getDateDistribution());
        distribution.setLot(lot);
        distribution.setAliment(aliment);
        distribution.setQuantite(distributionDTO.getQuantite());

        Double rationTheorique = CalculRationTheoriqueCible(distributionDTO);

        distribution.setRationTheorique(rationTheorique);

        distributionRepository.save(distribution);

        MouvementStock mouvementStock = createMouvementStock(distribution);

        mouvementStockService.create(mouvementStock);

        return distribution;

    }

    private MouvementStock createMouvementStock(Distribution distribution) {
        MouvementStock mouvementStock = new MouvementStock();
        mouvementStock.setAliment(distribution.getAliment());
        mouvementStock.setTypeMouvement(TypeMouvement.SORTIE);
        mouvementStock.setQuantite(distribution.getQuantite().doubleValue());
        mouvementStock.setCommentaire("Distribution aliment dans le lot #" + distribution.getLot().getId());
        return mouvementStock;
    }

    public Double calculGPQCible(DistributionDTO distributionDTO) {
        LotModels lot = lotRepository.findById(distributionDTO.getIdLot())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lot introuvable avec l'ID : " + distributionDTO.getIdLot()));

        Double poidsMoyenActuel = lot.getPoidsMoyenActuel() != null ? lot.getPoidsMoyenActuel() : 0.0;

        Double poidsMoyenCible = lot.getEspece().getPoidsCibleMoyen() != null
                ? lot.getEspece().getPoidsCibleMoyen().doubleValue()
                : 0.0;

        LocalDate dateRecolteEstimee = previsionRecolteService.estimerDateRecolte(lot.getId());

        if (dateRecolteEstimee == null) {
            System.err.println("Date de récolte estimée introuvable pour le lot avec l'ID : " + lot.getId());
            return 1.0;
        }

        Integer jourRestant = (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dateRecolteEstimee);

        if (jourRestant <= 0) {
            System.err.println("La date de récolte estimée est passée pour le lot avec l'ID : " + lot.getId());
            return 0.2; // evite infini
        }

        Double total = 0.0;

        total = (poidsMoyenCible - poidsMoyenActuel) / (double) jourRestant;

        if (total <= 0) {
            System.err
                    .println("Le poids moyen actuel est supérieur ou égal au poids moyen cible pour le lot avec l'ID : "
                            + lot.getId());
            return 0.2; // evite infini
        }

        return total;
    }

    public Double calculGainCible(DistributionDTO distributionDTO) {
        LotModels lot = lotRepository.findById(distributionDTO.getIdLot())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lot introuvable avec l'ID : " + distributionDTO.getIdLot()));

        Double gainCible = 0.0;

        Double gpqCible = calculGPQCible(distributionDTO);

        Integer quantitePoisson = lot.getEffectifActuel() != null ? lot.getEffectifActuel() : 0;

        gainCible = (gpqCible * quantitePoisson) / 1000.0;

        return gainCible;
    }

    public Double CalculRationTheoriqueCible(DistributionDTO distributionDTO) {
        Double rationTheoriqueCible = 0.0;

        Double gainCible = calculGainCible(distributionDTO);

        String paramCode = "ICA_SYSTEME";

        ParametreSysteme param = parametreSystemeRepository.findByCode(paramCode).orElse(null);

        Double ica = 1.3;

        if (param != null) {
            try {
                String icaStr = param.getValeur();

                icaStr = icaStr.replace(",", ".");

                ica = Double.parseDouble(icaStr);

            } catch (NumberFormatException e) {
                System.err.println("Erreur lors de la conversion de la valeur ICA : " + e.getMessage());
                ica = 1.3;
            }

        } else {
            System.err.println("Paramètre ICA introuvable. Utilisation de la valeur par défaut : 1.3");
        }

        rationTheoriqueCible = gainCible.doubleValue() * ica;
        return rationTheoriqueCible;

    }

}
