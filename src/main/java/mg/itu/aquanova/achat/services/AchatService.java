package mg.itu.aquanova.achat.services;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.repositories.AchatRepository;

@Service
public class AchatService {

    private final AchatRepository achatRepository;

    public AchatService(
        AchatRepository achatRepository
    ) {
        this.achatRepository = achatRepository;
    }

    public boolean estDejaUtilise(Long id) {
        return this.achatRepository.existsByCategorieDepenseId(id);
    }

}
