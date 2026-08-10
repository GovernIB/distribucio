package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.BustiaDefaultResourceEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;

/**
 * Repositori per a la gestió de bústies per defecte de tipus {@link BustiaDefaultResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface BustiaDefaultResourceRepository extends BaseRepository<BustiaDefaultResourceEntity, Long> {

	BustiaDefaultResourceEntity findByEntitatAndUsuari(EntitatResourceEntity entitat, UsuariResourceEntity usuari);

}
