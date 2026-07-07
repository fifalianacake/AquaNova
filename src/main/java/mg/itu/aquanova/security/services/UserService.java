package mg.itu.aquanova.security.services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mg.itu.aquanova.security.models.User;
import mg.itu.aquanova.security.repositories.UserRepository;
import mg.itu.aquanova.security.repositories.UserRoleRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null && !existing.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        // Le rôle assigné n'est pas un historique à conserver : on le retire avant
        // de supprimer l'utilisateur pour ne pas violer la contrainte de clé étrangère.
        userRoleRepository.findByUserId(id).ifPresent(userRoleRepository::delete);
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}