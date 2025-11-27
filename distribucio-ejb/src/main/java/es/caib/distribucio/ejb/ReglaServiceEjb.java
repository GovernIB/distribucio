/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.ReglaService;
import lombok.experimental.Delegate;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ReglaServiceEjb extends AbstractServiceEjb<ReglaService> implements ReglaService {

	@Delegate
	private ReglaService delegateService = null;

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto create(
			Long entitatId,
			ReglaDto regla) {
		return delegateService.create(
				entitatId,
				regla);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto update(Long entitatId, ReglaDto regla) throws NotFoundException {
		return delegateService.update(entitatId, regla);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			String sia,
			boolean activa) throws NotFoundException {
		return delegateService.updateActiva(
				entitatId,
				reglaId,
				sia,
				activa);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto updateActivaPresencial(Long entitatId,
			Long reglaId,
			boolean activa,
			ReglaPresencialEnumDto presencial,
			String sia)
			 throws NotFoundException {
		return delegateService.updateActivaPresencial(
				entitatId,
				reglaId,
				activa,
				presencial,
				sia);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto delete(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegateService.delete(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegateService.moveUp(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegateService.moveDown(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException {
		return delegateService.moveTo(
				entitatId,
				reglaId,
				posicio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public List<String> aplicarManualment(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return delegateService.aplicarManualment(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId) {
		return delegateService.findOne(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbEntitatPaginat(
				entitatId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndUnitatFiltreCodi(Long entitatId, String unitatCodi) {
		return delegateService.findByEntitatAndUnitatFiltreCodi(
				entitatId,
				unitatCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndUnitatDestiCodi(Long entitatId, String unitatCodi) {
		return delegateService.findByEntitatAndUnitatDestiCodi(
				entitatId,
				unitatCodi);
	}

	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndBackofficeDestiId(Long entitatId, Long backofficeDestiId) {
		return delegateService.findByEntitatAndBackofficeDestiId(
				entitatId,
				backofficeDestiId);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<ReglaDto> findAmbFiltrePaginat(
			Long entitatId,
			ReglaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<Long> findReglaIds(Long entitatId,ReglaFiltreDto filtre) {
		return delegateService.findReglaIds(
				entitatId, 
				filtre);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto) {
		return delegateService.simularReglaAplicacio(
				registreSimulatDto);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public Map<String, List<ReglaDto>> findReglesByCodiProcediment(List<String> procediments) {
		return delegateService.findReglesByCodiProcediment(procediments);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaBackofficeByProcediment(String procedimentCodi) {
		return delegateService.findReglaBackofficeByProcediment(procedimentCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaBackofficeByServei(String serveiCodi) {
		return delegateService.findReglaBackofficeByServei(serveiCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaBackofficeByCodiSia(String siaCodi) {
		return delegateService.findReglaBackofficeByCodiSia(siaCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaByProcediment (String procedimentCodi) {
		return delegateService.findReglaByProcediment(procedimentCodi);
	}

	protected void setDelegateService(ReglaService delegateService) {
		this.delegateService = delegateService;
	}

}
