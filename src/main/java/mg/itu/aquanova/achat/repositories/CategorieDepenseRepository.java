package mg.itu.aquanova.achat.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.achat.models.CategorieDepense;

public interface CategorieDepenseRepository extends JpaRepository<CategorieDepense, Long> {
    Optional<CategorieDepense> findByCode(String code);
    List<CategorieDepense> findAllByOrderByLibelleAsc();
}
