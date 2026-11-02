package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.BackofficeResourceEntity;

import java.util.Optional;

public interface BackofficeResourceRepository extends BaseRepository<BackofficeResourceEntity, Long> {
    Optional<BackofficeResourceEntity> findByCodi(String codi);
}