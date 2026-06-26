package mg.itu.aquanova.production.repositories;

import mg.itu.aquanova.production.models.TypeRecoltes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRecoltesRepository extends JpaRepository<TypeRecoltes, Long> {
}