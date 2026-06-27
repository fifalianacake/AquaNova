package mg.itu.aquanova.stock.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import mg.itu.aquanova.stock.models.*;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
}