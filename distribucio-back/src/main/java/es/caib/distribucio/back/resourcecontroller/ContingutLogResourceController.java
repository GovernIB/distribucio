package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ContingutLogResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta del log d'accions d'un contingut (només de lectura -- veure les
 * restriccions d'accés a {@link ContingutLogResource}).
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/contingutLogs")
public class ContingutLogResourceController extends BaseMutableResourceController<ContingutLogResource, Long> {

}
