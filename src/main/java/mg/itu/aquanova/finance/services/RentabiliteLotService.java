package mg.itu.aquanova.finance.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.production.models.Recoltes;
import mg.itu.aquanova.production.repositories.LotRepository;
import mg.itu.aquanova.production.repositories.RecoltesRepository;
import mg.itu.aquanova.production.services.LotService;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.Vente;
import mg.itu.aquanova.vente.repositories.VenteRepository;

public class RentabiliteLotService {
    
    private final LotRepository lotRepository;
    private final LotService lotService;
    private final RecoltesRepository recoltesRepository;
    private final VenteRepository venteRepository;

    public RentabiliteLotService(
        LotRepository lotRepository,
        LotService lotService,
        RecoltesRepository recoltesRepository,
        VenteRepository venteRepository
    ) {
        this.lotRepository = lotRepository;
        this.lotService = lotService;
        this.recoltesRepository = recoltesRepository;
        this.venteRepository = venteRepository;
    }

    public BigDecimal calculerChiffreAffairesLot(Long idLot) {
        LotModels lots = lotService.trouverParId(idLot);
        List<Recoltes> listRecoltes = recoltesRepository.findByLot(lots);
        
        BigDecimal chiffreAffaire = BigDecimal.ZERO;
        if(listRecoltes.size() != 0) {
            for(Recoltes recolte : listRecoltes) {
                Optional<Vente> venteOptional = venteRepository.findByRecolte(recolte);

                if (venteOptional.isEmpty()) {
                    continue; 
                }
                Vente vente = venteOptional.get();

                if(vente.getStatutVente().getCode().equals(StatutVenteEnum.PAYEE)) {
                    BigDecimal montantVente = BigDecimal.valueOf(vente.getMontantTotal());
                    chiffreAffaire = chiffreAffaire.add(montantVente);
                }
                
            }
        }

        return chiffreAffaire;
    }

    // public BigDecimal calculerProfitLot(Long idLot) {
    //     BigDecimal profit = BigDecimal.ZERO;
    //     BigDecimal chiffreAffaire = calculerChiffreAffairesLot(idLot);


    //     return profit;
    // }

    // public BigDecimal calculerMargeBeneficiaire(Long idLot) {
    //     BigDecimal marge = BigDecimal.ZERO;


    //     return marge;
    // }

    // public BigDecimal calculerRentabilite(Long idLot) {
    //     BigDecimal rentabilite = BigDecimal.ZERO;


    //     return rentabilite;
    // }

    // public String getStatutRentabilite(Long idLot) {

    //     return "";
    // }

}
