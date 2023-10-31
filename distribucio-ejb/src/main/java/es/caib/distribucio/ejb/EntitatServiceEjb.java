/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.EntitatService;
import lombok.experimental.Delegate;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntitatServiceEjb extends AbstractServiceEjb<EntitatService> implements EntitatService {

	@Delegate
	private EntitatService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto create(EntitatDto entitat) {
		return delegateService.create(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto update(
			EntitatDto entitat) {
		return delegateService.update(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return delegateService.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto delete(
			Long id) {
		return delegateService.delete(id);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto findByIdWithLogo(Long id) throws NotFoundException {
		return delegateService.findByIdWithLogo(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findByCodiDir3(String codiDir3) {
		return delegateService.findByCodiDir3(codiDir3);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegateService.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<EntitatDto> findAccessiblesUsuariActual() {
		return delegateService.findAccessiblesUsuariActual();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<PermisDto> findPermisSuper(Long id) {
		return delegateService.findPermisSuper(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		delegateService.updatePermisSuper(
				id,
				permis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		delegateService.deletePermisSuper(
				id,
				permisId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<PermisDto> findPermisAdmin(Long id) {
		return delegateService.findPermisAdmin(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) {
		delegateService.updatePermisAdmin(
				id,
				permis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void deletePermisAdmin(
			Long id,
			Long permisId) {
		delegateService.deletePermisAdmin(
				id,
				permisId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void evictEntitatsAccessiblesUsuari() {
		delegateService.evictEntitatsAccessiblesUsuari();
	}

	@Override
	@RolesAllowed("**")
	public void setConfigEntitat(EntitatDto entitatDto) {
		delegateService.setConfigEntitat(entitatDto);
	}

	protected void setDelegateService(EntitatService delegateService) {
		this.delegateService = delegateService;
	}

}
