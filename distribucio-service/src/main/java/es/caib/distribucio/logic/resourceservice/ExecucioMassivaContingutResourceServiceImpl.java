package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaContingutResource;
import es.caib.distribucio.logic.intf.resourceservice.ExecucioMassivaContingutResourceService;
import es.caib.distribucio.persist.resourceentity.ExecucioMassivaContingutResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaContingutResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaContingutResource, Long, ExecucioMassivaContingutResourceEntity> implements ExecucioMassivaContingutResourceService {

    /**
     * Autoemplena estat/dataCreacio en crear un contingut d'execució massiva des d'una pantalla React.
     * Només s'omple si el client no ho ha enviat.
     */
    @Override
    protected void beforeCreateSave(
            ExecucioMassivaContingutResourceEntity entity,
            ExecucioMassivaContingutResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        if (entity.getEstat() == null) {
            entity.setEstat(ExecucioMassivaContingutEstatDto.PENDENT);
        }
        if (entity.getDataCreacio() == null) {
            entity.setDataCreacio(new Date());
        }
    }

}