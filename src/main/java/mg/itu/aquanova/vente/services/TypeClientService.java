package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.models.TypeClient;
import mg.itu.aquanova.vente.repositories.TypeClientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TypeClientService {
    private final TypeClientRepository repository;

    public TypeClientService(TypeClientRepository repository) { this.repository = repository; }
    public List<TypeClient> listerTout() { return repository.findAll(); }
}