package mg.itu.aquanova.production.services;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.production.repositories.TransfertRepository;

import mg.itu.aquanova.production.models.TransfertModels;

@Service
public class TransfertService {

    private final TransfertRepository transfertRepository;

    public TransfertService(TransfertRepository transfertRepository) {
        this.transfertRepository = transfertRepository;
    }

    public List<TransfertModels> getAllTransferts() {
        return transfertRepository.findAll();
    }

    public TransfertModels getTransfertById(Long id) {
        return transfertRepository.findById(id).orElse(null);
    }

    public TransfertModels saveTransfert(TransfertModels transfert) {
        return transfertRepository.save(transfert);
    }

    public void deleteTransfert(Long id) {
        transfertRepository.deleteById(id);
    }

    public TransfertModels updateTransfert(Long id, TransfertModels updatedTransfert) {
        TransfertModels existingTransfert = getTransfertById(id);

        if (existingTransfert != null) {
            existingTransfert.setDateTransfert(updatedTransfert.getDateTransfert());
            existingTransfert.setEffectif(updatedTransfert.getEffectif());
            existingTransfert.setPoidsMoyen(updatedTransfert.getPoidsMoyen());
            existingTransfert.setLotSource(updatedTransfert.getLotSource());
            existingTransfert.setLotDestination(updatedTransfert.getLotDestination());
            existingTransfert.setBassinSource(updatedTransfert.getBassinSource());
            existingTransfert.setBassinDestination(updatedTransfert.getBassinDestination());

            return transfertRepository.save(existingTransfert);
        }
        return null;
    }

}
