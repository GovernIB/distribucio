package es.caib.distribucio.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ExcepcioLogResource;

/**
 * Servei REST de consulta del log d'excepcions (només de lectura -- veure les restriccions
 * d'accés a {@link ExcepcioLogResource}).
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/excepcions")
public class ExcepcioLogResourceController extends BaseMutableResourceController<ExcepcioLogResource, Long> {

}
