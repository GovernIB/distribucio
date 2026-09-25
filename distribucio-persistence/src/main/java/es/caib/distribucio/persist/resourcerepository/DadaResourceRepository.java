package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.DadaResourceEntity;

import java.util.List;

public interface DadaResourceRepository extends BaseRepository<DadaResourceEntity, Long> {
    List<DadaResourceEntity> findByRegistreId(Long id);
}