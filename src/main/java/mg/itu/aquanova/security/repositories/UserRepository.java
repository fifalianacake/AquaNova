package mg.itu.aquanova.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.itu.aquanova.security.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByEmail(String email);

}
