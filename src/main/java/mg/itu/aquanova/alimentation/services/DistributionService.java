package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.admin.models.ParametreSysteme;
import mg.itu.aquanova.admin.repositories.ParametreSystemeRepository;
import mg.itu.aquanova.alimentation.dto.DistributionDTO;
import mg.itu.aquanova.alimentation.models.DistributionModels;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Pese;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.PeseRepository;
import mg.itu.aquanova.referentiel.repositories.TypeAlimentRepository;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.models.TypeMouvement;
import mg.itu.aquanova.referentiel.repositories.AlimentRepository;
import mg.itu.aquanova.alimentation.services.MouvementService;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DistributionService {

    private final DistributionRepository distributionRepository;
    private final LotRepository lotRepository;
    private final MouvementService mouvementStockService;
    private final AlimentRepository alimentRepository;
    private final PeseRepository peseRepository;
    private final ParametreSystemeRepository parametreSystemeRepository;

    public DistributionService(DistributionRepository distributionRepository,
            LotRepository lotRepository,
            TypeAlimentRepository typeAlimentRepository,
            MouvementService mouvementStockService,
            AlimentRepository alimentRepository,
            PeseRepository peseRepository,
            ParametreSystemeRepository parametreSystemeRepository) {

        this.distributionRepository = distributionRepository;
        this.lotRepository = lotRepository;
        this.mouvementStockService = mouvementStockService;
        this.alimentRepository = alimentRepository;
        this.peseRepository = peseRepository;
        this.parametreSystemeRepository = parametreSystemeRepository;
    }

    public List<DistributionModels> getAllDistributions() {
        return distributionRepository.findAll();
    }

    public DistributionModels getDistributionById(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribution introuvable avec l'ID : " + id));
    }

    public void deleteDistribution(Long id) {
        distributionRepository.deleteById(id);
    }

    @Transactional
    public void saveOrUpdateDistribution(DistributionDTO distributionDTO) {
        DistributionModels distribution = new DistributionModels();

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

        Double rationTheorique = CalculRationTheorique(distributionDTO);
        distribution.setRationTheorique(BigDecimal.valueOf(rationTheorique));

        distributionRepository.save(distribution);
    }

    private Double calculGPQ(LotModels lot) {

        Double total = 0.0;

        List<Pese> listPese = peseRepository.findByLotIdOrderByDatePeseeDesc(lot.getId());
        Double poidsMoyenActuel = lot.getPoidsMoyenActuel() != null ? lot.getPoidsMoyenActuel().doubleValue() : 0.0;
        Double poidsMoyenAvant = 0.0;

        Long jourEcoule = 0L;

        if (listPese != null && !listPese.isEmpty()) {

            Pese dernierPese = listPese.get(0);

            poidsMoyenAvant = dernierPese.getPoidsMoyen() != null ? dernierPese.getPoidsMoyen().doubleValue() : 0.0;

            jourEcoule = java.time.temporal.ChronoUnit.DAYS.between(dernierPese.getDatePesee(),
                    java.time.LocalDate.now());

        } else {

            poidsMoyenAvant = lot.getPoidsMoyenInitial() != null ? lot.getPoidsMoyenInitial().doubleValue() : 0.0;

            if (lot.getDateMiseEnCharge() != null) {
                jourEcoule = java.time.temporal.ChronoUnit.DAYS.between(lot.getDateMiseEnCharge(),
                        java.time.LocalDate.now());
            }

        }

        if (jourEcoule > 0) {
            total = (poidsMoyenActuel - poidsMoyenAvant) / jourEcoule;
        } else {
            total = (poidsMoyenActuel - poidsMoyenAvant);
        }

        if (total <= 0) {
            total = 0.5;
        }

        return total;

    }

    private Double calculGainOfLot(LotModels lot) {
        Double gain = 0.0;

        Double gpq = calculGPQ(lot);

        Integer quantitePoisson = lot.getEffectifActuel() != null ? lot.getEffectifActuel() : 0;

        gain = gpq * quantitePoisson / 1000;

        return gain;
    }

    public Double CalculRationTheorique(DistributionDTO distributionDTO) {
        Double rationTheorique = 0.0;

        LotModels lot = lotRepository.findById(distributionDTO.getIdLot())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lot introuvable avec l'ID : " + distributionDTO.getIdLot()));

        Double gain = calculGainOfLot(lot);

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

        rationTheorique = gain * ica;
        return rationTheorique;

    }

    public DistributionModels saveDistribution(DistributionDTO distributionDTO) {

        DistributionModels distribution = new DistributionModels();

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

        Double rationTheorique = CalculRationTheorique(distributionDTO);
        BigDecimal rationTheoriqueBD = BigDecimal.valueOf(rationTheorique);
        distribution.setRationTheorique(rationTheoriqueBD);

        distributionRepository.save(distribution);

        MouvementStock mouvementStock = new MouvementStock();

        mouvementStock.setAliment(aliment);
        mouvementStock.setTypeMouvement(TypeMouvement.SORTIE);
        mouvementStock.setQuantite(distributionDTO.getQuantite().doubleValue());
        mouvementStock.setCommentaire("Distribution aliment dans le lot #" + distribution.getLot().getId());

        mouvementStockService.create(mouvementStock);

        return distribution;

    }

}