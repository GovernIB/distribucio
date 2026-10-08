package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.ExcepcioLogResource;

/**
 * Servei de consulta del log d'excepcions (només lectura -- veure les restriccions d'accés a
 * {@link ExcepcioLogResource}).
 *
 * @author Límit Tecnologies
 */
public interface ExcepcioLogResourceService extends MutableResourceService<ExcepcioLogResource, Long> {
}
