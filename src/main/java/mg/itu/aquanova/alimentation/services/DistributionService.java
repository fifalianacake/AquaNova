package mg.itu.aquanova.alimentation.services;

import mg.itu.aquanova.alimentation.models.DistributionModels;
import mg.itu.aquanova.alimentation.repositories.DistributionRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistributionService {

    private final DistributionRepository distributionRepository;

    public DistributionService(DistributionRepository distributionRepository) {
        this.distributionRepository = distributionRepository;
    }

    public List<DistributionModels> getAllDistributions() {
        return distributionRepository.findAll();
    }

    public DistributionModels getDistributionById(Long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribution introuvable avec l'ID : " + id));
    }

    public DistributionModels saveDistribution(DistributionModels distribution) {
        return distributionRepository.save(distribution);
    }

    public void deleteDistribution(Long id) {
        distributionRepository.deleteById(id);
    }

    public DistributionModels updateDistribution(Long id, DistributionModels updatedData) {
        DistributionModels existing = getDistributionById(id);
        if (existing != null) {
            existing.setDateDistribution(updatedData.getDateDistribution());
            existing.setLot(updatedData.getLot());
            existing.setAliment(updatedData.getAliment());
            existing.setQuantite(updatedData.getQuantite());
            existing.setRationTheorique(updatedData.getRationTheorique());
            return distributionRepository.save(existing);
        }
        return null;
    }
}