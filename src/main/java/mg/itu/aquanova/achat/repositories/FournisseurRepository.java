package mg.itu.aquanova.achat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.achat.models.Fournisseur;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    List<Fournisseur> findAllByOrderByNomAsc();
    List<Fournisseur> findByActifTrueOrderByNomAsc();
}
