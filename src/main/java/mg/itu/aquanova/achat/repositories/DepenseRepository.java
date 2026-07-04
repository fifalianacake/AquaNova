package mg.itu.aquanova.achat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.itu.aquanova.achat.models.Depense;

public interface DepenseRepository extends JpaRepository<Depense, Long>, JpaSpecificationExecutor<Depense> {
}
