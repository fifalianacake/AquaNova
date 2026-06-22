package mg.itu.aquanova.production.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mg.itu.aquanova.production.models.TypeRecoltes;
import mg.itu.aquanova.production.repositories.TypeRecoltesRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TypeRecolteService {

    private final TypeRecoltesRepository typeRecoltesRepository;

    @Autowired
    public TypeRecolteService(TypeRecoltesRepository typeRecoltesRepository) {
        this.typeRecoltesRepository = typeRecoltesRepository;
    }

    public TypeRecoltes saveTypeRecoltes(TypeRecoltes typeRecolte) {
        return typeRecoltesRepository.save(typeRecolte);
    }

    public List<TypeRecoltes> getAllTypeRecoltes() {
        return typeRecoltesRepository.findAll();
    }

    public TypeRecoltes getTypeRecolteById(Long id) {
        return typeRecoltesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Type de récolte introuvable avec l'id : " + id));
    }

    public TypeRecoltes updateTypeRecoltes(Long id, TypeRecoltes typeRecolteDetails) {
        TypeRecoltes existingType = getTypeRecolteById(id); 
        
        existingType.setLibelle(typeRecolteDetails.getLibelle());
        existingType.setDescription(typeRecolteDetails.getDescription());
        
        return typeRecoltesRepository.save(existingType);
    }

    public void deleteTypeRecoltes(Long id) {
        TypeRecoltes typeRecolte = getTypeRecolteById(id); 
        typeRecoltesRepository.delete(typeRecolte);
    }
}