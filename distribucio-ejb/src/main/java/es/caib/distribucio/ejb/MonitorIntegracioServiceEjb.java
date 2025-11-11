/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnosticDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDto;
import es.caib.distribucio.logic.intf.dto.IntegracioFiltreDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import lombok.experimental.Delegate;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorIntegracioServiceEjb extends AbstractServiceEjb<MonitorIntegracioService> implements MonitorIntegracioService {

	@Delegate
	private MonitorIntegracioService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<IntegracioDto> integracioFindAll() {
		return delegateService.integracioFindAll();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<IntegracioDto> findPerDiagnostic() {
		return delegateService.findPerDiagnostic();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
		return delegateService.create(monitorIntegracio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public MonitorIntegracioDto findById(Long id) throws NotFoundException {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, IntegracioFiltreDto integracioFiltreDto) {
		return delegateService.findPaginat(paginacioParams, integracioFiltreDto);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public Map<String, Integer> countErrors(int numeroHores) {
		return delegateService.countErrors(numeroHores);
	}
	
	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public int esborrarDadesAntigues(Date data) {
		return delegateService.esborrarDadesAntigues(data);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public int delete(String codi) {
		return delegateService.delete(codi);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public IntegracioDiagnosticDto diagnostic(String codiIntegracio, UsuariDto usuari) {
		return delegateService.diagnostic(codiIntegracio, usuari);
	}
	
	@Override
	@RolesAllowed("**")
	public Map<String, Integer> countCanvisEstatFromUser(String user) {
		return delegateService.countCanvisEstatFromUser(user);
	}

	protected void setDelegateService(MonitorIntegracioService delegateService) {
		this.delegateService = delegateService;
	}

}
