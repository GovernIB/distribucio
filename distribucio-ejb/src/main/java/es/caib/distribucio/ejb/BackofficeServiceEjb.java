package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.BackofficeService;
import lombok.experimental.Delegate;

/**
 * Implementació de BackofficeService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BackofficeServiceEjb extends AbstractServiceEjb<BackofficeService> implements BackofficeService {

	@Delegate
	private BackofficeService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto create(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public Exception provar(
			Long entitatId, 
			Long backofficeId) throws NotFoundException {
		return delegateService.provar(entitatId, backofficeId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto update(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BackofficeDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REGLA })
	public BackofficeDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<BackofficeDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<BackofficeDto> findByEntitat(Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REGLA })
	public BackofficeDto findByCodi(
			Long entitatId,
			String backofficeCodi) throws NotFoundException {
		return delegateService.findByCodi(entitatId, backofficeCodi);
	}

	protected void setDelegateService(BackofficeService delegateService) {
		this.delegateService = delegateService;
	}

}
