package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.model.SiaResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SiaResourceService extends MutableResourceService<SiaResource, Long> {
    SiaResource findByEntitatIdAndCodiSia(Long entitatId, String cosiSia);

    PaginaDto<SiaResource> findPage(
            String quickFilter,
            String filter,
            String[] namedQueries,
            String[] perspectives,
            PaginacioParamsDto pageable);
}
