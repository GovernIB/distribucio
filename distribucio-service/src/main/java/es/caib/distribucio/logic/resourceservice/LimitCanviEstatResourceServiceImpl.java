package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.LimitCanviEstatResource;
import es.caib.distribucio.logic.intf.resourceservice.LimitCanviEstatResourceService;
import es.caib.distribucio.persist.resourceentity.LimitCanviEstatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de recurs per a la gestió de límits de canvi d'estat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LimitCanviEstatResourceServiceImpl
		extends BaseMutableResourceService<LimitCanviEstatResource, Long, LimitCanviEstatResourceEntity>
		implements LimitCanviEstatResourceService {

}
