package mg.itu.aquanova.achat.services;

import org.springframework.stereotype.Service;

import mg.itu.aquanova.achat.repositories.AchatRepository;

@Service
public class AchatAlevinService {

    private final AchatRepository achatRepository;

    public AchatAlevinService(
        AchatRepository achatRepository
    ) {
        this.achatRepository = achatRepository;
    }

    
}
