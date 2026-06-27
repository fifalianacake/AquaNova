package mg.itu.aquanova.stock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.stock.models.Aliment;

public interface AlimentRepository extends JpaRepository<Aliment, Long> {

}