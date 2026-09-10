package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaContingutResource;
import es.caib.distribucio.logic.intf.resourceservice.ExecucioMassivaContingutResourceService;
import es.caib.distribucio.persist.resourceentity.ExecucioMassivaContingutResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaContingutResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaContingutResource, Long, ExecucioMassivaContingutResourceEntity> implements ExecucioMassivaContingutResourceService {

}