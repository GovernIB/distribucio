package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.resourceservice.BustiaResourceService;
import lombok.experimental.Delegate;

public class BustiaResourceServiceEjb extends AbstractServiceEjb<BustiaResourceService> implements BustiaResourceService {

    @Delegate
    private BustiaResourceService delegate = null;

    @Override
    protected void setDelegateService(BustiaResourceService delegate) {
        this.delegate = delegate;
    }

}
