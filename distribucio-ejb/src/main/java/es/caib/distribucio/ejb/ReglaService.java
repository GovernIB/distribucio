/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ReglaService extends AbstractService<es.caib.distribucio.logic.intf.service.ReglaService> implements es.caib.distribucio.logic.intf.service.ReglaService {

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto create(
			Long entitatId,
			ReglaDto regla) {
		return getDelegateService().create(
				entitatId,
				regla);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto update(Long entitatId, ReglaDto regla) throws NotFoundException {
		return getDelegateService().update(entitatId, regla);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			String sia,
			boolean activa) throws NotFoundException {
		return getDelegateService().updateActiva(
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
		return getDelegateService().updateActivaPresencial(
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
		return getDelegateService().delete(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return getDelegateService().moveUp(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return getDelegateService().moveDown(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException {
		return getDelegateService().moveTo(
				entitatId,
				reglaId,
				posicio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public List<String> aplicarManualment(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		return getDelegateService().aplicarManualment(
				entitatId,
				reglaId);
	}

    @Override
    @RolesAllowed(BaseConfig.ROLE_ADMIN)
    public List<RegistreDto> consultaRegistresAplicaRegla(
            Long entitatId,
            Long reglaId) throws NotFoundException {
        return getDelegateService().consultaRegistresAplicaRegla(
                entitatId,
                reglaId);
    }

    @Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId) {
		return getDelegateService().findOne(
				entitatId,
				reglaId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbEntitatPaginat(
				entitatId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndUnitatFiltreCodi(Long entitatId, String unitatCodi) {
		return getDelegateService().findByEntitatAndUnitatFiltreCodi(
				entitatId,
				unitatCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndUnitatDestiCodi(Long entitatId, String unitatCodi) {
		return getDelegateService().findByEntitatAndUnitatDestiCodi(
				entitatId,
				unitatCodi);
	}

	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ReglaDto> findByEntitatAndBackofficeDestiId(Long entitatId, Long backofficeDestiId) {
		return getDelegateService().findByEntitatAndBackofficeDestiId(
				entitatId,
				backofficeDestiId);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<ReglaDto> findAmbFiltrePaginat(
			Long entitatId,
			ReglaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<Long> findReglaIds(Long entitatId,ReglaFiltreDto filtre) {
		return getDelegateService().findReglaIds(
				entitatId, 
				filtre);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto) {
		return getDelegateService().simularReglaAplicacio(
				registreSimulatDto);
	}

    @Override
    @RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
    public List<ReglaMatchDto> findReglesByCodisSiaAndTramits(List<String> sias, List<String> tramits) {
        return getDelegateService().findReglesByCodisSiaAndTramits(sias, tramits);
    }

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaBackofficeByCodiSiaAndTramit(String siaCodi, String tramit) {
		return getDelegateService().findReglaBackofficeByCodiSiaAndTramit(siaCodi, tramit);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_REGLA })
	public List<ReglaDto> findReglaBackofficeByCodiSiaAndAnyTramit(String siaCodi, String tramit) {
		return getDelegateService().findReglaBackofficeByCodiSiaAndAnyTramit(siaCodi, tramit);
	}

}
