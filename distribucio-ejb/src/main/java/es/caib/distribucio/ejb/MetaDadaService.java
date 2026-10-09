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

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MetaDadaService extends AbstractService<es.caib.distribucio.logic.intf.service.MetaDadaService> implements es.caib.distribucio.logic.intf.service.MetaDadaService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto create(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return getDelegateService().create(entitatId, metaDada);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto update(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return getDelegateService().update(entitatId, metaDada);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto delete(Long entitatId, Long metaDadaId) throws NotFoundException {
		return getDelegateService().delete(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto updateActiva(Long entitatId, Long metaDadaId, boolean activa) throws NotFoundException {
		return getDelegateService().updateActiva(entitatId, metaDadaId, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveUp(Long entitatId, Long metaDadaId) throws NotFoundException {
		getDelegateService().moveUp(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveDown(Long entitatId, Long metaDadaId) throws NotFoundException {
		getDelegateService().moveDown(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void moveTo(Long entitatId, Long metaDadaId, int posicio) throws NotFoundException {
		getDelegateService().moveTo(entitatId, metaDadaId, posicio); 
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public MetaDadaDto findById(Long entitatId, Long metaDadaId) throws NotFoundException {
		return getDelegateService().findById(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("**")
	public MetaDadaDto findByCodi(Long entitatId, String codi) throws NotFoundException {
		return getDelegateService().findByCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public PaginaDto<MetaDadaDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return getDelegateService().findByEntitatPaginat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDadaDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}

}
