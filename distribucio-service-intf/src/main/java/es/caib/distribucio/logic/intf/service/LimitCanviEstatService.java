package es.caib.distribucio.logic.intf.service;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LimitCanviEstatService {

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    LimitCanviEstatDto findById(Long id);

//    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    LimitCanviEstatDto findByUsuariCodi(String usuariCodi);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    LimitCanviEstatDto create(LimitCanviEstatDto limitCanviEstatDto);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    LimitCanviEstatDto update(LimitCanviEstatDto limitCanviEstatDto);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    void delete(Long limitCanviEstatId);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
    public PaginaDto<LimitCanviEstatDto> findAllPaged(PaginacioParamsDto paginacioParams);
}
