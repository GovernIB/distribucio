package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.BustiaDefaultResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta i modificació de bústies per defecte.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/bustiesDefault")
public class BustiaDefaultResourceController extends BaseMutableResourceController<BustiaDefaultResource, Long> {

}
