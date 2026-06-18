package mg.itu.aquanova.security.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mg.itu.aquanova.security.models.RoleModels;

@Repository
public interface RoleRepository extends JpaRepository<RoleModels, Long> {
    Optional<RoleModels> findByName(String name);
}