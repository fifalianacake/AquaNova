package mg.itu.aquanova.vente.repositories;

import mg.itu.aquanova.vente.models.TypeClient;
import mg.itu.aquanova.vente.models.TypeClientEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeClientRepository extends JpaRepository<TypeClient, Long> {
    TypeClient findByCode(TypeClientEnum code);
}