package mg.itu.aquanova.achat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.itu.aquanova.achat.models.LigneAchat;

public interface LigneAchatRepository extends JpaRepository<LigneAchat, Long> {
    List<LigneAchat> findByAchatId(Long achatId);
}
