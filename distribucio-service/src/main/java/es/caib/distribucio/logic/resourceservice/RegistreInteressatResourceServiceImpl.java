package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.RegistreInteressatResource;
import es.caib.distribucio.logic.intf.resourceservice.RegistreInteressatResourceService;
import es.caib.distribucio.persist.resourceentity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class RegistreInteressatResourceServiceImpl extends BaseMutableResourceService<RegistreInteressatResource, Long, RegistreInteressatResourceEntity> implements RegistreInteressatResourceService {

    @PostConstruct
    public void init() {
    }

    @Override
    protected void afterConversion(RegistreInteressatResourceEntity entity, RegistreInteressatResource resource) {
        resource.setNomComplet( entity.getNomComplet() );
    }
}