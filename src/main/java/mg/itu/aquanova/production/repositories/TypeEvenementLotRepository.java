package mg.itu.aquanova.production.repositories;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TypeEvenementLotRepository extends JpaRepository<TypeEvenementLot, Long> {
    Optional<TypeEvenementLot> findByLibelle(TypeEvenementLot.LibelleEvenement libelle);

    boolean existsByLibelle(TypeEvenementLot.LibelleEvenement libelle);

    boolean existsByCode(String code);
}
