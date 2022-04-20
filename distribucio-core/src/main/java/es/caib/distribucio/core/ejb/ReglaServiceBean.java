/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaFiltreDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.ReglaService;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ReglaServiceBean implements ReglaService {

	@Autowired
	ReglaService delegate;



	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto create(
			Long entitatId,
			ReglaDto regla) {
		return delegate.create(
				entitatId,
				regla);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto update(
			Long entitatId,
			ReglaDto regla) throws NotFoundException {
		return delegate.update(
				entitatId,
				regla);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			boolean activa) throws NotFoundException {
		return delegate.updateActiva(
				entitatId,
				reglaId,
				activa);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto delete(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegate.delete(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegate.moveUp(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegate.moveDown(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException {
		return delegate.moveTo(
				entitatId,
				reglaId,
				posicio);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<String> aplicarManualment(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegate.aplicarManualment(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId) {
		return delegate.findOne(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbEntitatPaginat(
				entitatId,
				paginacioParams);
	}
	
	


	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<ReglaDto> findByEntitatAndUnitatCodi(Long entitatId, String unitatCodi) {
		return delegate.findByEntitatAndUnitatCodi(
				entitatId,
				unitatCodi);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<ReglaDto> findAmbFiltrePaginat(
			Long entitatId,
			ReglaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto) {
		return delegate.simularReglaAplicacio(
				registreSimulatDto);
	}
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public Map<String, List<ReglaDto>> findReglesByCodiProcediment(List<String> procediments) {
		return delegate.findReglesByCodiProcediment(procediments);
	}

	@Override
	@RolesAllowed("DIS_REGLA")
	public List<ReglaDto> findReglaBackofficeByProcediment(String procedimentCodi) {
		return delegate.findReglaBackofficeByProcediment(procedimentCodi);
	}

}
