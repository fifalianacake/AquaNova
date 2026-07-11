package mg.itu.aquanova.finance.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.repositories.LigneAchatRepository;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;
import mg.itu.aquanova.finance.dto.RentabiliteLotDTO;
import mg.itu.aquanova.finance.models.StatutRentabilite;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.services.LotFilter;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.vente.repositories.VenteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RentabiliteLotService {

    private static final int ECHELLE_MONTANT = 2;

    private final LotService lotService;
    private final LotRepository lotRepository;
    private final VenteRepository venteRepository;
    private final LigneAchatRepository ligneAchatRepository;
    private final DistributionRepository distributionRepository;

    public RentabiliteLotService(
            LotService lotService,
            LotRepository lotRepository,
            VenteRepository venteRepository,
            LigneAchatRepository ligneAchatRepository,
            DistributionRepository distributionRepository) {
        this.lotService = lotService;
        this.lotRepository = lotRepository;
        this.venteRepository = venteRepository;
        this.ligneAchatRepository = ligneAchatRepository;
        this.distributionRepository = distributionRepository;
    }

    public Page<RentabiliteLotDTO> lister(LotFilter filter, Pageable pageable) {
        Page<LotModels> lots = lotService.lister(filter, pageable);
        List<RentabiliteLotDTO> contenu = lots.stream()
                .map(this::construirePourLot)
                .collect(Collectors.toList());
        return new PageImpl<>(contenu, pageable, lots.getTotalElements());
    }

    public RentabiliteLotDTO construirePourLot(Long lotId) {
        LotModels lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot introuvable : " + lotId));
        return construirePourLot(lot);
    }

    public RentabiliteLotDTO construirePourLot(LotModels lot) {
        RentabiliteLotDTO dto = new RentabiliteLotDTO();
        dto.setLotId(lot.getId());
        dto.setLotCode(lot.getCode());
        dto.setEspece(lot.getEspece() != null ? lot.getEspece().getNom() : null);
        dto.setBassin(lot.getBassin() != null ? lot.getBassin().getReference() : null);
        dto.setStatutLot(lot.getStatutLot() != null && lot.getStatutLot().getLibelle() != null
                ? lot.getStatutLot().getLibelle().name()
                : null);

        BigDecimal chiffreAffaires = calculerChiffreAffairesLot(lot.getId());
        BigDecimal coutAlevins = calculerCoutAlevins(lot.getId());
        BigDecimal coutAlimentation = calculerCoutAlimentation(lot.getId());
        BigDecimal coutsDirects = coutAlevins.add(coutAlimentation);
        BigDecimal margeBrute = chiffreAffaires.subtract(coutsDirects);
        BigDecimal poidsVendu = calculerPoidsVenduLot(lot.getId());

        dto.setChiffreAffaires(arrondir(chiffreAffaires));
        dto.setCoutAlevins(arrondir(coutAlevins));
        dto.setCoutAlimentation(arrondir(coutAlimentation));
        dto.setCoutsDirects(arrondir(coutsDirects));
        dto.setMargeBrute(arrondir(margeBrute));
        dto.setPoidsVendu(poidsVendu);

        if (chiffreAffaires.signum() > 0) {
            dto.setTauxMargeBrute(margeBrute
                    .multiply(BigDecimal.valueOf(100))
                    .divide(chiffreAffaires, ECHELLE_MONTANT, RoundingMode.HALF_UP));
        }

        if (poidsVendu.signum() > 0) {
            dto.setCoutDirectParKgVendu(coutsDirects.divide(poidsVendu, ECHELLE_MONTANT, RoundingMode.HALF_UP));
        }

        dto.setStatutRentabilite(determinerStatut(chiffreAffaires, margeBrute));
        return dto;
    }

    private StatutRentabilite determinerStatut(BigDecimal chiffreAffaires, BigDecimal margeBrute) {
        if (chiffreAffaires.signum() <= 0) {
            return StatutRentabilite.NON_CALCULABLE;
        }
        return margeBrute.signum() >= 0 ? StatutRentabilite.RENTABLE : StatutRentabilite.DEFICITAIRE;
    }

    public BigDecimal calculerChiffreAffairesLot(Long lotId) {
        return versBigDecimal(venteRepository.sumChiffreAffairesParLot(lotId));
    }

    public BigDecimal calculerCoutAlevins(Long lotId) {
        BigDecimal montant = ligneAchatRepository.sumMontantParLot(lotId);
        return montant != null ? montant : BigDecimal.ZERO;
    }

    public BigDecimal calculerCoutAlimentation(Long lotId) {
        return versBigDecimal(distributionRepository.findTotalCoutAlimentByLotId(lotId));
    }

    private BigDecimal calculerPoidsVenduLot(Long lotId) {
        return versBigDecimal(venteRepository.sumPoidsVenduParLot(lotId));
    }

    private BigDecimal versBigDecimal(Double valeur) {
        return valeur != null ? BigDecimal.valueOf(valeur) : BigDecimal.ZERO;
    }

    private BigDecimal arrondir(BigDecimal valeur) {
        return valeur.setScale(ECHELLE_MONTANT, RoundingMode.HALF_UP);
    }
}
