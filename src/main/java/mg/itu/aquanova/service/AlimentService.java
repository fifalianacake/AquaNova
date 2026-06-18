package mg.itu.aquanova.service;

import mg.itu.aquanova.entity.Aliment;
import mg.itu.aquanova.repository.AlimentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlimentService {

    private final AlimentRepository alimentRepository;

    public AlimentService(AlimentRepository alimentRepository) {
        this.alimentRepository = alimentRepository;
    }

    public List<Aliment> findAll() {
        return alimentRepository.findAll();
    }

    public Optional<Aliment> findById(Long id) {
        return alimentRepository.findById(id);
    }

    public Aliment save(Aliment aliment) {
        return alimentRepository.save(aliment);
    }

    public void delete(Long id) {
        alimentRepository.deleteById(id);
    }
}