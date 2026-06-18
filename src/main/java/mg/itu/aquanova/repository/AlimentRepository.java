package mg.itu.aquanova.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.entity.Aliment;

public interface AlimentRepository
        extends JpaRepository<Aliment, Long> {
}
