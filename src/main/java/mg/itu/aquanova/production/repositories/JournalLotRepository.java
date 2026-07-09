package mg.itu.aquanova.production.repositories;
import mg.itu.aquanova.production.models.JournalLot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JournalLotRepository extends JpaRepository<JournalLot, Long>, JpaSpecificationExecutor<JournalLot> {
    List<JournalLot> findByLotIdOrderByDateEvenementAsc(Long lotId);

    List<JournalLot> findAllByOrderByDateEvenementAsc();
}
