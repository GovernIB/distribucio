package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió de bústies de tipus {@link ContingutResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ContingutResourceRepository extends BaseRepository<ContingutResourceEntity, Long> {
    List<ContingutResourceEntity> findAllByPareId(Long pareId);
}
