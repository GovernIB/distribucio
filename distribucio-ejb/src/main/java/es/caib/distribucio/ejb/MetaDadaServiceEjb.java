package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.MetaDadaDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import lombok.experimental.Delegate;

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MetaDadaServiceEjb extends AbstractServiceEjb<MetaDadaService> implements MetaDadaService {

	@Delegate
	private MetaDadaService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto create(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return delegateService.create(entitatId, metaDada);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto update(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return delegateService.update(entitatId, metaDada);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto delete(Long entitatId, Long metaDadaId) throws NotFoundException {
		return delegateService.delete(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto updateActiva(Long entitatId, Long metaDadaId, boolean activa) throws NotFoundException {
		return delegateService.updateActiva(entitatId, metaDadaId, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveUp(Long entitatId, Long metaDadaId) throws NotFoundException {
		delegateService.moveUp(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveDown(Long entitatId, Long metaDadaId) throws NotFoundException {
		delegateService.moveDown(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveTo(Long entitatId, Long metaDadaId, int posicio) throws NotFoundException {
		delegateService.moveTo(entitatId, metaDadaId, posicio); 
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto findById(Long entitatId, Long metaDadaId) throws NotFoundException {
		return delegateService.findById(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("**")
	public MetaDadaDto findByCodi(Long entitatId, String codi) throws NotFoundException {
		return delegateService.findByCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public PaginaDto<MetaDadaDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDadaDto> findByEntitat(Long entitatId) {
		return delegateService.findByEntitat(entitatId);
	}

	protected void setDelegateService(MetaDadaService delegateService) {
		this.delegateService = delegateService;
	}

}
