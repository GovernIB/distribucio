package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ServeiResourceEntity;

import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus {@link ServeiResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ServeiResourceRepository extends BaseRepository<ServeiResourceEntity, Long> {

    Optional<ServeiResourceEntity> findByEntitatIdAndCodiSia(Long entitatId, String codiSia);
}
