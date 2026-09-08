package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ContingutMovimentResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta dels moviments d'un contingut (només de lectura -- veure les
 * restriccions d'accés a {@link ContingutMovimentResource}).
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/contingutMoviments")
public class ContingutMovimentResourceController extends BaseMutableResourceController<ContingutMovimentResource, Long> {

}
