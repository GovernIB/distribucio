package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.EntitatResource;
import es.caib.distribucio.logic.intf.resourceservice.EntitatResourceService;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de consulta i modificació d'entitats via el motor genèric de recursos.
 *
 * @author Límit Tecnologies
 */
@Service
public class EntitatResourceServiceImpl
		extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity>
		implements EntitatResourceService {

}
