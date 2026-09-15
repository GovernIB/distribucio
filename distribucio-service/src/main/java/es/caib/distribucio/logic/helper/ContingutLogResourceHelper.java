package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ContingutLogResource;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;
import es.caib.distribucio.persist.resourceentity.ContingutLogResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContingutLogResourceHelper {

    private final RegistreResourceRepository registreResourceRepository;

    public void transferParams(ContingutLogResourceEntity entity, ContingutLogResource resource) {
        resource.setParams(
                entity.getParams().stream()
                        .map(ContingutLogParamEntity::getValor)
                        .collect(Collectors.toList()));
    }

    public void setLogText(ContingutLogResourceEntity entity, ContingutLogResource resource) {
        if (entity.getContingut().getTipus() != ContingutTipusEnumDto.REGISTRE) return;
        RegistreResourceEntity registre = registreResourceRepository.findById(entity.getContingut().getId()).get();
        I18nUtil i18nUtil = I18nUtil.getInstance();

        if (entity.getParams() != null && resource.getParams() == null)
            this.transferParams(entity, resource);

        StringBuilder sb = new StringBuilder();
        String usuari = entity.getCreatedBy() != null ? resource.getCreatedBy() + " - " + resource.getCreatedByFullName() : "-";
        switch(entity.getTipus()) {
            case CREACIO:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.creacio", new Object[] {resource.getParams().get(0)}));

                if (entity.getContingutMoviment() != null) {
                    if (entity.getContingutMoviment().getDestiId() != null) {
                        sb.append(" " + i18nUtil.getI18nMessage("contingut.log.resum.msg.iEsPosaALaBustia", new Object[] {entity.getContingutMoviment().getDestiNom()}));
                    }
                }

                break;
            case MOVIMENT:
            case REENVIAMENT:
                if (resource.getParams().contains("ORIGINAL")) {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.reenviar"));
                } else if (resource.getParams().contains("ORIGINAL_AMB_COPIA")) {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.reenviar.original"));
                } else if (resource.getParams().contains("COPIA")) {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.reenviar.copia"));
                }
                if (entity.getContingutMoviment() != null) {
                    if (entity.getContingutMoviment().getOrigenId() != null) {
                        sb.append(" ").append(i18nUtil.getI18nMessage("contingut.log.resum.msg.deLaBustia")).append(" \"");
                        sb.append(entity.getContingutMoviment().getOrigenNom()).append("\"");
                    }
                    if (entity.getContingutMoviment().getDestiId() != null) {
                        sb.append(" ").append(i18nUtil.getI18nMessage("contingut.log.resum.msg.aLaBustia")).append(" \"");
                        sb.append(entity.getContingutMoviment().getDestiNom()).append("\"");
                    }
                }
                break;
            case ENVIAMENT_EMAIL:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.enviamentEmail",
                        new Object[] {usuari,
                                resource.getParams().get(1)}));
                break;
            case MARCAMENT_PROCESSAT:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.marcamentProcessat", new Object[] {usuari}));
                break;
            case MARCAMENT_PENDENT:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.marcamentPendent", new Object[] {usuari}));
                break;
            case DISTRIBUCIO:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.distribucio"));
                break;
            case REGLA_APLICAR:

                String reglaTipus = null;
                if (resource.getParams().get(1).equals(ReglaTipusEnumDto.BUSTIA.toString())) {
                    reglaTipus = i18nUtil.getI18nMessage("regla.tipus.enum.BUSTIA");
                } else if (resource.getParams().get(1).equals(ReglaTipusEnumDto.UNITAT.toString())) {
                    reglaTipus = i18nUtil.getI18nMessage("regla.tipus.enum.UNITAT");
                } else if (resource.getParams().get(1).equals(ReglaTipusEnumDto.BACKOFFICE.toString())) {
                    reglaTipus = i18nUtil.getI18nMessage("regla.tipus.enum.BACKOFFICE");
                }

                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.reglaAplicar", new Object[] {resource.getParams().get(0), reglaTipus}));

                if (entity.getContingutMoviment() != null) {

                    String msg = i18nUtil.getI18nMessage("contingut.log.resum.msg.reenviar");
                    msg = msg.substring(0, 1).toLowerCase() + msg.substring(1);
                    sb.append(": " + msg);
                    if (entity.getContingutMoviment().getDestiId() != null) {
                        sb.append(" ").append(i18nUtil.getI18nMessage("contingut.log.resum.msg.aLaBustia")).append(" \"");
                        sb.append(entity.getContingutMoviment().getDestiNom()).append("\"");
                    }
                }


                break;
            case BACK_COMUNICADA:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_COMUNICADA"));
                break;
            case BACK_REBUDA:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_REBUDA"));
                break;
            case BACK_PROCESSADA:
                if ((registre!=null)&&(registre.getBackCodi()!=null)) {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_PROCESSADA_KNOW", new Object[] {registre.getBackCodi()}));
                } else {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_PROCESSADA"));
                }
                break;
            case BACK_REBUTJADA:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_REBUTJADA"));
                String motiuRebuig = resource.getParams() != null && resource.getParams().size() > 0 ?
                        resource.getParams().get(0)
                        :null;
                if (motiuRebuig != null && !motiuRebuig.trim().isEmpty()) {
                    sb.append(" ");
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.motiu", new Object[] {motiuRebuig}));
                }
                break;
            case BACK_ERROR:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.BACK_ERROR"));
                break;
            case AGAFAR:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.AGAFAR", new Object[] {resource.getParams().get(0)}));
                break;
            case ALLIBERAR:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.ALLIBERAR"));
                break;
            case DUPLICITAT:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.DUPLICITAT", new Object[] {resource.getParams().get(0), resource.getParams().get(1)}));
                break;
            case SOBREESCRIURE:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.SOBREESCRIURE", new Object[] {resource.getParams().get(0)}));
                break;
            case CLASSIFICAR:
                if (!resource.getParams().isEmpty()) {
                    if (RegistreClassificarTipusEnum.PROCEDIMENT.name().equals(resource.getParams().get(0))) {
                        if (resource.getParams().size() >= 2 && resource.getParams().get(1) != null) {
                            sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CLASSIFICAR.PROCEDIMENT", new Object[]{usuari, resource.getParams().get(1)}));
                        } else {
                            sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CLASSIFICAR.PROCEDIMENT.NULL", new Object[]{usuari}));
                        }
                    } else if (RegistreClassificarTipusEnum.SERVEI.name().equals(resource.getParams().get(0))) {
                        if (resource.getParams().size() >= 2 && resource.getParams().get(1) != null) {
                            sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CLASSIFICAR.SERVEI", new Object[]{usuari, resource.getParams().get(1)}));
                        } else {
                            sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CLASSIFICAR.SERVEI.NULL", new Object[]{usuari}));
                        }
                    }
                } else {
                    sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CLASSIFICAR", new Object[]{usuari}));
                }
                break;
            case CANVI_PENDENT:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.CANVI_PENDENT", new Object[] {resource.getParams().get(0), resource.getParams().get(1)}));
                break;
            default:
                sb.append(i18nUtil.getI18nMessage("contingut.log.resum.msg.accio")).append(": \"");
                sb.append(i18nUtil.getI18nMessage("log.tipus.enum." + entity.getTipus().name())).append("\"");
                if (resource.getParams().get(0) != null)
                    sb.append(" param1: \"").append(resource.getParams().get(0)).append("\"");
                if (resource.getParams().get(1) != null)
                    sb.append(" param2: \"").append(resource.getParams().get(1)).append("\"");
                break;
        }
        resource.setResum( sb.toString() );
    }
}
