package es.caib.distribucio.persist.repository;

import es.caib.distribucio.persist.entity.LimitCanviEstatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimitCanviEstatRepository extends JpaRepository<LimitCanviEstatEntity, Long> {
    Optional<LimitCanviEstatEntity> findByUsuariCodi(String usuariCodi);
}
