package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.PendentsArxiuResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/pendentsArxiu")
@Tag(name = "Anotacions pendents d'Arxiu", description = "Monitor d'anotacions pendents de guardar a Arxiu: històric de la tasca (només DIS_SUPER)")
public class PendentsArxiuResourceController extends BaseMutableResourceController<PendentsArxiuResource, String> {}
