package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ConfigTypeResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/configType")
@Tag(name = "ConfigType", description = "Servei de gestió de tipus de propietats configurables per l'aplicació")
public class ConfigTypeResourceController extends BaseMutableResourceController<ConfigTypeResource, String> {}