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

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntitatService extends AbstractService<es.caib.distribucio.logic.intf.service.EntitatService> implements es.caib.distribucio.logic.intf.service.EntitatService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto create(EntitatDto entitat) {
		return getDelegateService().create(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto update(
			EntitatDto entitat) {
		return getDelegateService().update(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto delete(
			Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto findByIdWithLogo(Long id) throws NotFoundException {
		return getDelegateService().findByIdWithLogo(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public EntitatDto findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public EntitatDto findByCodiDir3(String codiDir3) {
		return getDelegateService().findByCodiDir3(codiDir3);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<EntitatDto> findAccessiblesUsuariActual() {
		return getDelegateService().findAccessiblesUsuariActual();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<PermisDto> findPermisSuper(Long id) {
		return getDelegateService().findPermisSuper(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		getDelegateService().updatePermisSuper(
				id,
				permis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		getDelegateService().deletePermisSuper(
				id,
				permisId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<PermisDto> findPermisAdmin(Long id) {
		return getDelegateService().findPermisAdmin(id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) {
		getDelegateService().updatePermisAdmin(
				id,
				permis);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void deletePermisAdmin(
			Long id,
			Long permisId) {
		getDelegateService().deletePermisAdmin(
				id,
				permisId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void evictEntitatsAccessiblesUsuari() {
		getDelegateService().evictEntitatsAccessiblesUsuari();
	}

}
