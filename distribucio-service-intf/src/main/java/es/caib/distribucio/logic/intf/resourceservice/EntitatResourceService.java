package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.EntitatResource;

import java.util.Optional;

public interface EntitatResourceService extends MutableResourceService<EntitatResource, Long> {
    Optional<EntitatResource> findByCodi(String codi);
    Optional<EntitatResource> findByCodiDir3(String codiDir3);
}