package es.caib.distribucio.logic.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ExecucioMassivaResourceHelper {

    private final ExecucioMassivaService execucioMassivaService;

    private String construirParametres(Map<String, Object> params) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir els paràmetres a JSON", e);
        }
    }

    public void executarAccioMassivaRegistres(
            ExecucioMassivaTipusDto tipus,
            List<RegistreResourceEntity> registreList,
            Map<String, Object> map
    ) {
        /// TODO: validacion de registro
        this.crearExecucioMassivaRegistres(tipus, registreList, map);
    }

    private void crearExecucioMassivaRegistres(
            ExecucioMassivaTipusDto tipus,
            List<RegistreResourceEntity> registreList,
            Map<String, Object> map) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

        List<ExecucioMassivaContingutDto> continguts = new ArrayList<>();
        for (RegistreResourceEntity registre : registreList) {
            ExecucioMassivaContingutDto contingut = new ExecucioMassivaContingutDto();
            contingut.setDataCreacio(new Date());
            contingut.setDataInici(new Date());
            contingut.setElementId(registre.getId());
            contingut.setElementNom(registre.getNom());
            contingut.setElementTipus(ElementTipusEnumDto.REGISTRE);
            continguts.add(contingut);
        }

        ExecucioMassivaDto execucioMassiva = new ExecucioMassivaDto();
        execucioMassiva.setDataCreacio(new Date());
        execucioMassiva.setDataInici(new Date());
        execucioMassiva.setTipus( tipus );
        execucioMassiva.setContinguts(continguts);

        execucioMassiva.setParametres( this.construirParametres(map) );

        execucioMassivaService.crearExecucioMassiva(
                entitatActualId,
                execucioMassiva);
    }
}
