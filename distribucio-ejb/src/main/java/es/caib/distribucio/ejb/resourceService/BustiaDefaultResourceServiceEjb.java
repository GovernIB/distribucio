package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.resourceservice.BustiaDefaultResourceService;
import lombok.experimental.Delegate;

public class BustiaDefaultResourceServiceEjb extends AbstractServiceEjb<BustiaDefaultResourceService> implements BustiaDefaultResourceService {

    @Delegate
    private BustiaDefaultResourceService delegate = null;

    @Override
    protected void setDelegateService(BustiaDefaultResourceService delegate) {
        this.delegate = delegate;
    }

}
