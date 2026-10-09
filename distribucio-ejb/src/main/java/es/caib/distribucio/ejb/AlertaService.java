/**
 * 
 */
package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.dto.AlertaDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de AlertaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AlertaService extends AbstractService<es.caib.distribucio.logic.intf.service.AlertaService> implements es.caib.distribucio.logic.intf.service.AlertaService {

	@Override
	@RolesAllowed("**")
	public AlertaDto create(
			AlertaDto alerta) {
		return getDelegateService().create(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException {
		return getDelegateService().update(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto delete(
			Long id) throws NotFoundException {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto find(
			Long id) {
		return getDelegateService().find(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AlertaDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AlertaDto> findPaginatByLlegida(
			boolean llegida,
			Long contingutId,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginatByLlegida(
				llegida,
				contingutId,
				paginacioParams);
	}

}
