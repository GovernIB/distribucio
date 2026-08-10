package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.resourceservice.EntitatResourceService;
import lombok.experimental.Delegate;

public class EntitatResourceServiceEjb extends AbstractServiceEjb<EntitatResourceService> implements EntitatResourceService {

    @Delegate
    private EntitatResourceService delegate = null;

    @Override
    protected void setDelegateService(EntitatResourceService delegate) {
        this.delegate = delegate;
    }

}
