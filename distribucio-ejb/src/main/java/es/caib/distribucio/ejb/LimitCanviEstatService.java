package es.caib.distribucio.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;

/**
 * Implementació de LimitCanviEstatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class LimitCanviEstatService extends AbstractService<es.caib.distribucio.logic.intf.service.LimitCanviEstatService> implements es.caib.distribucio.logic.intf.service.LimitCanviEstatService {

	@Override
	@RolesAllowed("**")
	public LimitCanviEstatDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("**")
	public LimitCanviEstatDto findByUsuariCodi(String usuariCodi) {
		return getDelegateService().findByUsuariCodi(usuariCodi);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public LimitCanviEstatDto create(LimitCanviEstatDto limitCanviEstatDto) {
		return getDelegateService().create(limitCanviEstatDto);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public  LimitCanviEstatDto update(LimitCanviEstatDto limitCanviEstatDto) {
		return getDelegateService().update(limitCanviEstatDto);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public void delete(Long limitCanviEstatId) {
		getDelegateService().delete(limitCanviEstatId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
    public PaginaDto<LimitCanviEstatDto> findAllPaged(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaged(paginacioParams);
	}

}
