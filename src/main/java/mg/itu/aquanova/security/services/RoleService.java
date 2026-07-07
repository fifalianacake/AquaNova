package mg.itu.aquanova.security.services;

import org.springframework.stereotype.*;

import java.util.List;

import mg.itu.aquanova.security.models.RoleModels;

import mg.itu.aquanova.security.repositories.RoleRepository;
import mg.itu.aquanova.security.repositories.UserRoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public List<RoleModels> getAllRoles() {
        return roleRepository.findAll();
    }

    public RoleModels getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public RoleModels getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public RoleModels saveRole(RoleModels role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        if (userRoleRepository.existsByRoleId(id)) {
            throw new IllegalStateException(
                    "Impossible de supprimer ce rôle : il est encore assigné à au moins un utilisateur.");
        }
        roleRepository.deleteById(id);
    }

    



}
