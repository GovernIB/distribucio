/**
 * 
 */
package es.caib.distribucio.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.AlertaDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.AlertaService;

/**
 * Implementació de AlertaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AlertaServiceBean implements AlertaService {

	@Autowired
	AlertaService delegate;
	
	
	@Override
	public AlertaDto create(
			AlertaDto alerta) {
		return delegate.create(alerta);
	}
	
	@Override
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException {
		return delegate.update(alerta);
	}
	
	@Override
	public AlertaDto delete(
			Long id) throws NotFoundException {
		return delegate.delete(id);
	}
	
	@Override
	public AlertaDto find(
			Long id) {
		return delegate.find(id);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public PaginaDto<AlertaDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public PaginaDto<AlertaDto> findPaginatByLlegida(
			boolean llegida,
			Long contingutId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPaginatByLlegida(
				llegida,
				contingutId,
				paginacioParams);
	}

}