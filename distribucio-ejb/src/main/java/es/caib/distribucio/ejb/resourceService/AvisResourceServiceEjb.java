package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.resourceservice.AvisResourceService;
import lombok.experimental.Delegate;

public class AvisResourceServiceEjb extends AbstractServiceEjb<AvisResourceService> implements AvisResourceService {

    @Delegate
    private AvisResourceService delegate = null;

    @Override
    protected void setDelegateService(AvisResourceService delegate) {
        this.delegate = delegate;
    }

}