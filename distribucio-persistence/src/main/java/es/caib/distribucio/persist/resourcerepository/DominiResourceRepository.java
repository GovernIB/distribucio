package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.DominiResourceEntity;

import java.util.Optional;

public interface DominiResourceRepository extends BaseRepository<DominiResourceEntity, Long> {
    Optional<DominiResourceEntity> findByCodi(String codi);
}