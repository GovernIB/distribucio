package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.BackofficeResource;

import java.util.Optional;

public interface BackofficeResourceService extends MutableResourceService<BackofficeResource, Long> {
    Optional<BackofficeResource> findByCodi(String codi);
}