package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.MetriquesResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/metriques")
@Tag(name = "Mètriques", description = "Mètriques de l'aplicació: timers del registre de Dropwizard (només DIS_SUPER)")
public class MetriquesResourceController extends BaseMutableResourceController<MetriquesResource, String> {}
