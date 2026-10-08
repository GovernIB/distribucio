package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.persist.resourceentity.ConfigTypeResourceEntity;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ConfigTypeResource;
import es.caib.distribucio.logic.intf.resourceservice.ConfigTypeResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigTypeResourceServiceImpl extends BaseMutableResourceService<ConfigTypeResource, String, ConfigTypeResourceEntity> implements ConfigTypeResourceService {

}
