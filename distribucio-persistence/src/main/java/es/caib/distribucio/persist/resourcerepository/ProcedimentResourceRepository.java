package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ProcedimentResourceEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus {@link ProcedimentResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ProcedimentResourceRepository extends BaseRepository<ProcedimentResourceEntity, Long> {

    Optional<ProcedimentResourceEntity> findByEntitatIdAndCodiSia(Long entitatId, String codiSia);
}
