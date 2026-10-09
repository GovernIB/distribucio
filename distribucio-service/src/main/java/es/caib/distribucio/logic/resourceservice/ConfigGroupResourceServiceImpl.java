package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.persist.resourceentity.ConfigGroupResourceEntity;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ConfigGroupResource;
import es.caib.distribucio.logic.intf.resourceservice.ConfigGroupResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigGroupResourceServiceImpl extends BaseMutableResourceService<ConfigGroupResource, String, ConfigGroupResourceEntity> implements ConfigGroupResourceService {
    @Override
    protected void afterConversion(ConfigGroupResourceEntity entity, ConfigGroupResource resource) {
        resource.setChildrens(entity.getChildren() != null ?entity.getChildren().size() :0);
    }
}
