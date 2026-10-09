package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.UnitatOrganitzativaResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/unitatsOrganitzatives")
public class UnitatOrganitzativaResourceController extends BaseMutableResourceController<UnitatOrganitzativaResource, Long> {
}
