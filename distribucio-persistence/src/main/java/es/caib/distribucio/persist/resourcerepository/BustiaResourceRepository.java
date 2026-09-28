package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.UnitatOrganitzativaResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió de bústies de tipus {@link BustiaResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface BustiaResourceRepository extends BaseRepository<BustiaResourceEntity, Long> {
    BustiaResourceEntity findByEntitatAndUnitatOrganitzativaAndPareNull(
            EntitatResourceEntity entitat,
            UnitatOrganitzativaResourceEntity unitatOrganitzativa);
    List<BustiaResourceEntity> findByEntitatIdAndUnitatOrganitzativaIdAndPareNotNull(
            Long entitatId,
            Long unitatOrganitzativaId);
}
