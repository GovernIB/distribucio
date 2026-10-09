package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.MetaDadaResourceEntity;

import java.util.List;

public interface MetaDadaResourceRepository extends BaseRepository<MetaDadaResourceEntity, Long> {
    List<MetaDadaResourceEntity> findByEntitatIdOrderByOrdreAsc(Long entitatId);
    int countByEntitatId(Long entitatId);
}