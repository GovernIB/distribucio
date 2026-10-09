/**
 * 
 */
package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AvisDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AvisService extends AbstractService<es.caib.distribucio.logic.intf.service.AvisService> implements es.caib.distribucio.logic.intf.service.AvisService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto create(AvisDto avis) {
		return getDelegateService().create(avis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto update(AvisDto avis) {
		return getDelegateService().update(avis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto updateActiva(Long id, boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto delete(Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AvisDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActive() {
		return getDelegateService().findActive();
	}

}
