package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.SiaResource;

public interface SiaResourceService extends MutableResourceService<SiaResource, Long> {
    SiaResource findByEntitatIdAndCodiSia(Long entitatId, String cosiSia);
}
