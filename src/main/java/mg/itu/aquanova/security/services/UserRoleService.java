package mg.itu.aquanova.security.services;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public UserRoleModels saveUserRole(UserRoleModels userRole) {
        return userRoleRepository.save(userRole);
    }

    public void deleteUserRole(Long id) {
        userRoleRepository.deleteById(id);
    }

}
