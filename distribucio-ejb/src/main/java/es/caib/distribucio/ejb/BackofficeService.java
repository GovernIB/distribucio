package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.BackofficeFiltreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de BackofficeService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BackofficeService extends AbstractService<es.caib.distribucio.logic.intf.service.BackofficeService> implements es.caib.distribucio.logic.intf.service.BackofficeService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto create(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return getDelegateService().create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public Exception provar(
			Long entitatId, 
			Long backofficeId) throws NotFoundException {
		return getDelegateService().provar(entitatId, backofficeId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto update(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return getDelegateService().update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return getDelegateService().delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REGLA })
	public BackofficeDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return getDelegateService().findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<BackofficeDto> findByEntitatPaginat(
			Long entitatId,
            BackofficeFiltreDto filtre,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return getDelegateService().findByEntitatPaginat(
				entitatId,
                filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<Long> findBackofficeIds(
			Long entitatId,
            BackofficeFiltreDto filtre)
			throws NotFoundException {
		return getDelegateService().findBackofficeIds(
				entitatId,
                filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<BackofficeDto> findByEntitat(Long entitatId) throws NotFoundException {
		return getDelegateService().findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REGLA })
	public BackofficeDto findByCodi(
			Long entitatId,
			String backofficeCodi) throws NotFoundException {
		return getDelegateService().findByCodi(entitatId, backofficeCodi);
	}

}
