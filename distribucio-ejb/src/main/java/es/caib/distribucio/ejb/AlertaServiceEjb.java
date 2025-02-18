/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.AlertaDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.AlertaService;
import lombok.experimental.Delegate;

/**
 * Implementació de AlertaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AlertaServiceEjb extends AbstractServiceEjb<AlertaService> implements AlertaService {

	@Delegate
	private AlertaService delegateService = null;

	@Override
	@RolesAllowed("**")
	public AlertaDto create(
			AlertaDto alerta) {
		return delegateService.create(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException {
		return delegateService.update(alerta);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto delete(
			Long id) throws NotFoundException {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AlertaDto find(
			Long id) {
		return delegateService.find(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AlertaDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AlertaDto> findPaginatByLlegida(
			boolean llegida,
			Long contingutId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginatByLlegida(
				llegida,
				contingutId,
				paginacioParams);
	}

	protected void setDelegateService(AlertaService delegateService) {
		this.delegateService = delegateService;
	}

}
