package mg.itu.aquanova.security.repositories;

import mg.itu.aquanova.security.models.RoleModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleModels, Long> {
    
    Optional<RoleModels> findByName(String name);
}
