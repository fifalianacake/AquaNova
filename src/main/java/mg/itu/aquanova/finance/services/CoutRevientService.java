package mg.itu.aquanova.finance.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.models.LigneAchat;
import mg.itu.aquanova.achat.repositories.LigneAchatRepository;
import mg.itu.aquanova.alimentation.models.Distribution;
import mg.itu.aquanova.alimentation.models.MouvementStock;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.alimentation.repositories.MouvementStockRepository;
import mg.itu.aquanova.finance.dto.CoutRevientLotDTO;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.repositories.RecoltesRepository;
import mg.itu.aquanova.production.services.LotFilter;
import mg.itu.aquanova.production.services.LotService;

@Service
public class CoutRevientService {

    private final LotService lotService;
    private final LigneAchatRepository ligneAchatRepository;
    private final DistributionRepository distributionRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final mg.itu.aquanova.achat.repositories.DepenseRepository depenseRepository;
    private final RecoltesRepository recoltesRepository;

    public CoutRevientService(
            LotService lotService,
            LigneAchatRepository ligneAchatRepository,
            DistributionRepository distributionRepository,
            MouvementStockRepository mouvementStockRepository,
            mg.itu.aquanova.achat.repositories.DepenseRepository depenseRepository,
            RecoltesRepository recoltesRepository) {
        this.lotService = lotService;
        this.ligneAchatRepository = ligneAchatRepository;
        this.distributionRepository = distributionRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.depenseRepository = depenseRepository;
        this.recoltesRepository = recoltesRepository;
    }

    public Page<CoutRevientLotDTO> listerLotsAvecCout(LotFilter filter, Pageable pageable) {
        Page<LotModels> lots = lotService.lister(filter, pageable);
        List<CoutRevientLotDTO> dtoList = lots.stream()
                .map(this::construireCoutRevientPourLot)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, lots.getTotalElements());
    }

    public CoutRevientLotDTO construireCoutRevientPourLot(LotModels lot) {
        CoutRevientLotDTO dto = new CoutRevientLotDTO();
        dto.setLotId(lot.getId());
        dto.setLotCode(lot.getCode());
        dto.setEspece(lot.getEspece() != null ? lot.getEspece().getNom() : null);
        dto.setBassin(lot.getBassin() != null ? lot.getBassin().getReference() : null);
        dto.setStatut(lot.getStatutLot() != null && lot.getStatutLot().getLibelle() != null? lot.getStatutLot().getLibelle().name(): null);
        dto.setDateMiseEnCharge(lot.getDateMiseEnCharge());
        dto.setEffectifActuel(lot.getEffectifActuel());

        BigDecimal totalAchat = calculerTotalAchatPourLot(lot.getId());
        BigDecimal totalAlimentation = calculerTotalAlimentationPourLot(lot.getId());
        BigDecimal totalDepensesImputees = calculerTotalDepensesImputeesPourLot(lot);
        BigDecimal totalPoidsRecolte = calculerTotalPoidsRecoltePourLot(lot.getId());

        dto.setTotalAchat(totalAchat);
        dto.setTotalDistribution(totalAlimentation);
        dto.setTotalPoidsRecolte(totalPoidsRecolte);
        dto.setTotalDepenses(totalDepensesImputees);

        BigDecimal baseCout = totalAchat.add(totalAlimentation).add(totalDepensesImputees);
        if (totalPoidsRecolte != null && totalPoidsRecolte.signum() > 0) {
            dto.setCoutRevientParKg(baseCout.divide(totalPoidsRecolte, 2, RoundingMode.HALF_UP));
        } else {
            dto.setCoutRevientParKg(BigDecimal.ZERO);
        }

        if (lot.getEffectifActuel() != null && lot.getEffectifActuel() > 0) {
            dto.setCoutRevientParIndividu(
                    baseCout.divide(BigDecimal.valueOf(lot.getEffectifActuel()), 2, RoundingMode.HALF_UP));
        } else {
            dto.setCoutRevientParIndividu(BigDecimal.ZERO);
        }

        return dto;
    }

    private BigDecimal calculerTotalAchatPourLot(Long lotId) {
        if (lotId == null) {
            return BigDecimal.ZERO;
        }
        List<LigneAchat> lignes = ligneAchatRepository.findByLotId(lotId);
        return lignes.stream()
                .map(this::calculerMontantLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerMontantLigne(LigneAchat ligne) {
        if (ligne == null) {
            return BigDecimal.ZERO;
        }
        if (ligne.getMontantLigne() != null) {
            return ligne.getMontantLigne();
        }
        if (ligne.getQuantite() != null && ligne.getPrixUnitaire() != null) {
            return ligne.getQuantite().multiply(ligne.getPrixUnitaire());
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculerTotalDistributionPourLot(Long lotId) {
        if (lotId == null) {
            return BigDecimal.ZERO;
        }
        List<Distribution> distributions = distributionRepository.findByLotId(lotId);
        return distributions.stream()
                .map(this::calculerCoutDistribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerCoutDistribution(Distribution distribution) {
        if (distribution == null || distribution.getQuantite() == null || distribution.getAliment() == null
                || distribution.getAliment().getPrixUnitaire() == null) {
            return BigDecimal.ZERO;
        }
        return distribution.getQuantite().multiply(BigDecimal.valueOf(distribution.getAliment().getPrixUnitaire()));
    }

    private BigDecimal calculerTotalAlimentationPourLot(Long lotId) {
        if (lotId == null) return BigDecimal.ZERO;
        List<Distribution> distributions = distributionRepository.findByLotId(lotId);
        return distributions.stream().map(d -> {
            java.util.Optional<MouvementStock> mv = mouvementStockRepository.findByDistributionId(d.getId());
            if (mv.isPresent() && mv.get().getQuantite() != null && d.getAliment() != null && d.getAliment().getPrixUnitaire() != null) {
                return BigDecimal.valueOf(mv.get().getQuantite()).multiply(BigDecimal.valueOf(d.getAliment().getPrixUnitaire()));
            }
            if (d.getQuantite() != null && d.getAliment() != null && d.getAliment().getPrixUnitaire() != null) {
                return d.getQuantite().multiply(BigDecimal.valueOf(d.getAliment().getPrixUnitaire()));
            }
            return BigDecimal.ZERO;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalDepensesImputeesPourLot(LotModels lot) {
        if (lot == null) return BigDecimal.ZERO;
        String code = lot.getCode() != null ? lot.getCode().toLowerCase() : null;
        List<mg.itu.aquanova.achat.models.Depense> depenses = depenseRepository.findAll();
        return depenses.stream().filter(d -> {
            if (code == null) return false;
            if (d.getReference() != null && d.getReference().toLowerCase().contains(code)) return true;
            if (d.getObservation() != null && d.getObservation().toLowerCase().contains(code)) return true;
            if (d.getLibelle() != null && d.getLibelle().toLowerCase().contains(code)) return true;
            return false;
        }).map(d -> d.getMontant() != null ? d.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculerTotalPoidsRecoltePourLot(Long lotId) {
        if (lotId == null) {
            return BigDecimal.ZERO;
        }
        List<Recoltes> recoltes = recoltesRepository.findByLotId(lotId);
        return recoltes.stream()
                .map(Recoltes::getPoidsTotal)
                .filter(p -> p != null)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
