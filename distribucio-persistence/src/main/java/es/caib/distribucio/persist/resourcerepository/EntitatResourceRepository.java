package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.logic.intf.model.EntitatResource;
import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Repositori per a la gestió d'entitats de tipus {@link EntitatResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface EntitatResourceRepository extends BaseRepository<EntitatResourceEntity, Long> {

    Optional<EntitatResourceEntity> findByCodi(String codi);
    Optional<EntitatResourceEntity> findByCodiDir3(String codiDir3);

}
