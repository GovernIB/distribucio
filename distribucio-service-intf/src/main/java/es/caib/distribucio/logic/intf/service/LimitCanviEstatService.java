package es.caib.distribucio.logic.intf.service;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

/** Servei per gestionar els límits de canvis d'estat per usuari d'aplicacio en les opcions del super usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface LimitCanviEstatService {

	public LimitCanviEstatDto findById(Long id);

	public LimitCanviEstatDto findByUsuariCodi(String usuariCodi);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    public LimitCanviEstatDto create(LimitCanviEstatDto limitCanviEstatDto);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    public  LimitCanviEstatDto update(LimitCanviEstatDto limitCanviEstatDto);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    public void delete(Long limitCanviEstatId);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    public PaginaDto<LimitCanviEstatDto> findAllPaged(PaginacioParamsDto paginacioParams);
}
