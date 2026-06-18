package mg.itu.aquanova.security.services;

import java.util.List;

import org.springframework.stereotype.*;

import mg.itu.aquanova.security.models.UserModels;
import mg.itu.aquanova.security.repositories.UserRepository;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModels> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModels getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserModels saveUser(UserModels user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserModels getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    
}
