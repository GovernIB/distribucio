package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ContingutLogResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió d'entitats de tipus {@link ContingutLogResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ContingutLogResourceRepository extends BaseRepository<ContingutLogResourceEntity, Long> {
    List<ContingutLogResourceEntity> findAllByContingutIdOrderByCreatedDateAsc(Long contingutId);
}
