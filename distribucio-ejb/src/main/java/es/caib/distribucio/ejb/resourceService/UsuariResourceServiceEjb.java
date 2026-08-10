package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import lombok.experimental.Delegate;

public class UsuariResourceServiceEjb  extends AbstractServiceEjb<UsuariResourceService> implements UsuariResourceService {

    @Delegate
    private UsuariResourceService delegate = null;

    @Override
    protected void setDelegateService(UsuariResourceService delegate) {
        this.delegate = delegate;
    }
}
