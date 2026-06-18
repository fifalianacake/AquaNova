package mg.itu.aquanova.security.services;

import org.springframework.stereotype.*;

import java.util.List;

import mg.itu.aquanova.security.models.RoleModels;

import mg.itu.aquanova.security.repositories.RoleRepository;

@Service
public class RoleService {
    
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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
        roleRepository.deleteById(id);
    }

    



}
