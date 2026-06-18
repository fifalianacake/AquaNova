package mg.itu.aquanova.admin.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.admin.models.ParametreSysteme;

public interface ParametreSystemeRepository extends JpaRepository<ParametreSysteme, Long> {
    Optional<ParametreSysteme> findByCode(String code);

    boolean existsByCode(String code);
}
