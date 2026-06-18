package mg.itu.aquanova.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.entity.Lot;

public interface LotRepository
        extends JpaRepository<Lot, Long> {
}
