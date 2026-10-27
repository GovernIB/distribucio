package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.resourceentity.SiaResourceEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus {@link SiaResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface SiaResourceRepository extends BaseRepository<SiaResourceEntity, Long> {

    Optional<SiaResourceEntity> findByEntitatIdAndCodi(Long entitatId, String codi);
    Optional<SiaResourceEntity> findByEntitatIdAndCodiSia(Long entitatId, String codiSia);

    List<SiaResourceEntity> findAllByEntitatIdAndEstatAndTipus(
            Long entitatId,
            ProcedimentEstatEnumDto estat,
            RegistreClassificarTipusEnum tipus);
}
