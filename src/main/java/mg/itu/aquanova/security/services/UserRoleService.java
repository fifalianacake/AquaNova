package mg.itu.aquanova.security.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.repositories.UserRoleRepository;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public List<UserRoleModels> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    public UserRoleModels getUserRoleByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public UserRoleModels saveUserRole(UserRoleModels userRole) {
        if (userRole.getUser() == null || userRole.getUser().getId() == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }
        if (userRole.getRole() == null || userRole.getRole().getId() == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire.");
        }

        // Un utilisateur ne doit avoir qu'un seul rôle actif : on réassigne l'existant
        // au lieu d'en créer un second, sinon getUserRoleByUserId() (utilisée au login)
        // devient ambiguë et plante.
        return userRoleRepository.findByUserId(userRole.getUser().getId())
                .map(existing -> {
                    existing.setRole(userRole.getRole());
                    return userRoleRepository.save(existing);
                })
                .orElseGet(() -> userRoleRepository.save(userRole));
    }

    public void deleteUserRole(Long id) {
        userRoleRepository.deleteById(id);
    }

}
