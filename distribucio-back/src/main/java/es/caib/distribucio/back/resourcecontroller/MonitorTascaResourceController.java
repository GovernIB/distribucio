package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.MonitorTascaResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/monitorTasques")
@Tag(name = "Monitor de tasques", description = "Monitorització i reinici de les tasques en segon pla (només DIS_SUPER)")
public class MonitorTascaResourceController extends BaseMutableResourceController<MonitorTascaResource, String> {}
