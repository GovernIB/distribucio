package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.model.AvisResource;
import es.caib.distribucio.logic.intf.resourceservice.AvisResourceService;
import es.caib.distribucio.persist.resourcerepository.AvisResourceRepository;
import es.caib.distribucio.persist.resourceentity.AvisResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de consulta i modificació d'avisos via el motor genèric de recursos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvisResourceServiceImpl
        extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity>
        implements AvisResourceService {

    private final AvisResourceRepository avisResourceRepository;

    @PostConstruct
    public void init() {
        register(AvisResource.ACTION_ACTIVAR_CODE, new ActivaActionExecutor());
        register(AvisResource.ACTION_DESACTIVAR_CODE, new ActivaActionExecutor());
        register(AvisResource.ACTION_ACCIO_MASSIVA_CODE, new AccioMassivaActionExecutor());
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<AvisResource> findActive() {
//        List<AvisResourceEntity> entities = avisResourceRepository
//                .findByActiuTrueAndDataIniciLessThanEqualAndDataFinalIsNullOrDataFinalGreaterThanEqual(
//                        DateUtils.truncate(new Date(), Calendar.DATE));
//        return entitiesToResources(entities);
//    }

    @Override
    protected void afterCreateSave(
            AvisResourceEntity entity,
            AvisResource resource,
            Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers,
            boolean anyOrderChanged) {
        // notificar, si correspon
    }

    @Override
    protected void afterUpdateSave(
            AvisResourceEntity entity,
            AvisResource resource,
            Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers,
            boolean anyOrderChanged) {
        // notificar, si correspon
    }

    @Override
    protected void afterDelete(
            AvisResourceEntity entity,
            Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers) {
        // notificar, si correspon
    }

    /**
     * Activa o desactiva l'avís segons el codi de l'acció executada, l'equivalent d'avis/{id}/enable
     * i avis/{id}/disable de la interfície JSP (AvisController.enable/disable).
     * <p>
     * Les accions no tenen formulari (no declaren formClass), de manera que {@code params} sempre
     * és null i el front les executa directament sobre la fila, sense cap diàleg.
     */
    private class ActivaActionExecutor implements ActionExecutor<AvisResourceEntity, Serializable, Serializable> {

        @Override
        public void onChange(
                Serializable id,
                Serializable previous,
                String fieldName,
                Object fieldValue,
                Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                Serializable target) {
            // Sense formulari no hi ha cap camp que pugui canviar.
        }

        @Override
        public Serializable exec(
                String code,
                AvisResourceEntity entity,
                Serializable params) throws ActionExecutionException {
            if (AvisResource.ACTION_ACTIVAR_CODE.equals(code)) {
                entity.setActiu(true);
            } else if (AvisResource.ACTION_DESACTIVAR_CODE.equals(code)) {
                entity.setActiu(false);
            } else {
                throw new ActionExecutionException(
                        AvisResource.class,
                        entity.getId(),
                        code,
                        "Codi d'acció desconegut");
            }

            avisResourceRepository.save(entity);
            // notificar
            return null;
        }

    }

    /**
     * Executa una acció massiva sobre múltiples avisos, l'equivalent de l'accioMassiva de la
     * interfície JSP (AvisController.accioMassiva).
     * <p>
     * Permet activar, desactivar o eliminar múltiples avisos en una sola operació.
     */
    private class AccioMassivaActionExecutor
            implements ActionExecutor<AvisResourceEntity, AvisResource.FormAccioMassiva, Serializable> {

        @Override
        public void onChange(
                Serializable id,
                AvisResource.FormAccioMassiva previous,
                String fieldName,
                Object fieldValue,
                Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                AvisResource.FormAccioMassiva target) {
            // El formulari no té cap camp que depengui dels altres.
        }

        @Override
        public Serializable exec(
                String code,
                AvisResourceEntity entity,
                AvisResource.FormAccioMassiva params) throws ActionExecutionException {
            List<AvisResourceEntity> avisEntities = avisResourceRepository.findAllById(params.getIds());

            if ("activar".equalsIgnoreCase(params.getAccio())) {
                avisEntities.forEach(avisEntity -> avisEntity.setActiu(true));
                avisResourceRepository.saveAll(avisEntities);
            } else if ("desactivar".equalsIgnoreCase(params.getAccio())) {
                avisEntities.forEach(avisEntity -> avisEntity.setActiu(false));
                avisResourceRepository.saveAll(avisEntities);
            } else if ("eliminar".equalsIgnoreCase(params.getAccio())) {
                avisResourceRepository.deleteAllById(params.getIds());
            } else {
                throw new ActionExecutionException(
                        AvisResource.class,
                        null,
                        code,
                        "Tipus d'acció massiva desconegut: " + params.getAccio());
            }

            // notificar una vegada
            return null;
        }

    }

}
