/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AvisDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.service.AvisService;
import lombok.experimental.Delegate;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AvisServiceEjb extends AbstractServiceEjb<AvisService> implements AvisService {

	@Delegate
	private AvisService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto create(AvisDto avis) {
		return delegateService.create(avis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto update(AvisDto avis) {
		return delegateService.update(avis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegateService.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public AvisDto delete(Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public AvisDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActive() {
		return delegateService.findActive();
	}

	protected void setDelegateService(AvisService delegateService) {
		this.delegateService = delegateService;
	}

}
