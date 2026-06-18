package mg.itu.aquanova.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.security.models.UserModels;

@Repository
public interface UserRepository extends JpaRepository<UserModels, Long> {
    
    UserModels findByEmail(String email);

}
