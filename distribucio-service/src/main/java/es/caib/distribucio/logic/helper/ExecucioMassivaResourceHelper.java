package es.caib.distribucio.logic.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExecucioMassivaResourceHelper {

    private final ExecucioMassivaService execucioMassivaService;
    private final AuthenticationHelper authenticationHelper;
    private final AclEntryResourceService aclEntryResourceService;

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
    ) throws Exception {
        /// TODO: validacion de registro
        List<RegistreResourceEntity> filteredList = registreList;
        if (!List.of(authenticationHelper.getCurrentUserRoles()).contains(BaseConfig.ROLE_ADMIN)) {
            filteredList = registreList.stream()
                    .filter(registre ->
                            aclEntryResourceService.anyPermissionGranted(
                                    ResourceType.BUSTIA,
                                    registre.getPare().getId(),
                                    List.of(PermissionEnum.WRITE),
                                    authenticationHelper.getCurrentUserName(),
                                    List.of(authenticationHelper.getCurrentUserRoles())
                            ))
                    .collect(Collectors.toList());
        }

        if (filteredList.isEmpty()) {
            throw new Exception(I18nUtil.getInstance().getI18nMessage("accio.massiva.controller.error.permis.bustia.descripcio"));
        }

        this.crearExecucioMassivaRegistres(tipus, filteredList, map);
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
