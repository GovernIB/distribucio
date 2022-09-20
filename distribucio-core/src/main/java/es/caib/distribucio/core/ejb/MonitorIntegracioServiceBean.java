/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.IntegracioDto;
import es.caib.distribucio.core.api.dto.IntegracioFiltreDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MonitorIntegracioServiceBean implements MonitorIntegracioService {

	@Autowired
	MonitorIntegracioService delegate;

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}

	@Override
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
		return delegate.create(monitorIntegracio);
	}

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public MonitorIntegracioDto findById(Long id) throws NotFoundException {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, IntegracioFiltreDto integracioFiltreDto) {
		return delegate.findPaginat(paginacioParams, integracioFiltreDto);
	}

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public Map<String, Integer> countErrors(int numeroHores) {
		return delegate.countErrors(numeroHores);
	}
	
	@Override
	public int esborrarDadesAntigues(Date data) {
		return delegate.esborrarDadesAntigues(data);
	}

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public int delete(String codi) {
		return delegate.delete(codi);
	}

}
