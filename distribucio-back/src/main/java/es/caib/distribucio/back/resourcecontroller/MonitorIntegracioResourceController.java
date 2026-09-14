package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.MonitorIntegracioResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta del monitor d'integracions (només de lectura -- veure les restriccions
 * d'accés a {@link MonitorIntegracioResource}).
 * <p>
 * L'acció {@link MonitorIntegracioResource#ACTION_COUNT_ERRORS_CODE} (comptador d'errors per codi
 * d'integració) queda exposada automàticament pel controller genèric a
 * {@code POST /artifacts/action/COUNT_ERRORS}.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/monitorIntegracions")
public class MonitorIntegracioResourceController extends BaseMutableResourceController<MonitorIntegracioResource, Long> {

}
