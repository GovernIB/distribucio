package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.RegistreInteressatResourceEntity;

import java.util.List;

public interface RegistreInteressatResourceRepository extends BaseRepository<RegistreInteressatResourceEntity, Long> {

	List<RegistreInteressatResourceEntity> findByRegistreId(Long registreId);

}
