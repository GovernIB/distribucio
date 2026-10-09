package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.MonitorFilResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/monitorFils")
@Tag(name = "Monitor de fils", description = "Monitorització dels fils d'execució i del sistema (només DIS_SUPER)")
public class MonitorFilResourceController extends BaseMutableResourceController<MonitorFilResource, Long> {}
