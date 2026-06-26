package mg.itu.aquanova.production.repositories;
import mg.itu.aquanova.production.models.JournalLot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalLotRepository extends JpaRepository<JournalLot, Long> {
    List<JournalLot> findByLotIdOrderByDateEvenementDesc(Long lotId);
}