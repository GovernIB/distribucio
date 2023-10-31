/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AvisDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;

/**
 * Declaració dels mètodes per a la gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisService {

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	AvisDto create(AvisDto avis);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	AvisDto update(AvisDto avis);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	AvisDto updateActiva(Long id, boolean activa);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	AvisDto delete(Long id);

	@PreAuthorize("isAuthenticated()")
	AvisDto findById(Long id);

	@PreAuthorize("isAuthenticated()")
	PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	List<AvisDto> findActive();


}
