package es.caib.distribucio.ejb;


import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.service.LimitCanviEstatService;
import lombok.experimental.Delegate;

/**
 * Implementació de LimitCanviEstatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class LimitCanviEstatServiceEjb extends AbstractServiceEjb<LimitCanviEstatService> implements LimitCanviEstatService {

	@Delegate
	private LimitCanviEstatService delegateService = null;

	@Override
	@RolesAllowed("**")
	public LimitCanviEstatDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("**")
	public LimitCanviEstatDto findByUsuariCodi(String usuariCodi) {
		return delegateService.findByUsuariCodi(usuariCodi);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public LimitCanviEstatDto create(LimitCanviEstatDto limitCanviEstatDto) {
		return delegateService.create(limitCanviEstatDto);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public  LimitCanviEstatDto update(LimitCanviEstatDto limitCanviEstatDto) {
		return delegateService.update(limitCanviEstatDto);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public void delete(Long limitCanviEstatId) {
		delegateService.delete(limitCanviEstatId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public PaginaDto<LimitCanviEstatDto> findAllPaged(PaginacioParamsDto paginacioParams) {
		return delegateService.findAllPaged(paginacioParams);
	}
	
	protected void setDelegateService(LimitCanviEstatService delegateService) {
		this.delegateService = delegateService;
	}

}
