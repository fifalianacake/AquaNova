package mg.itu.aquanova.security.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.security.models.UserRoleModels;


public interface UserRoleRepository  extends JpaRepository<UserRoleModels, Long> {
    
    Optional<UserRoleModels> findByUserId(Long userId);

    boolean existsByRoleId(Long roleId);
}
