package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.AlertaResource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/alertes")
public class AlertaResourceController extends BaseMutableResourceController<AlertaResource, Long> {}