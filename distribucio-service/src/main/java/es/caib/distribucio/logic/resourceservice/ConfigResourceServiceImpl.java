package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.config.SegonPlaConfig;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.ConfigResource;
import es.caib.distribucio.logic.intf.resourceservice.ConfigResourceService;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.resourceentity.ConfigResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl extends BaseMutableResourceService<ConfigResource, String, ConfigResourceEntity> implements ConfigResourceService {

    private final ConfigHelper configHelper;
    private final SegonPlaConfig schedulingConfig;

    @PostConstruct
    public void init() {
        register(ConfigResource.PERSPECTIVE_ALTERNATIVE_VALUE_CODE, new AlternativeValuePerspectiveApplicator());
        register(ConfigResource.ACTION_SYNC_JBOSS_CODE, new SincronitzarActionExecutor());
        register(ConfigResource.ACTION_RESTART_TASKS_CODE, new RestartTaskActionExecutor());
    }

    private String getKeyBase(String keyActual, String entitatCodi) {
		String keyBase = keyActual + "";
		if (Utils.hasValue(entitatCodi)) {
			keyBase = keyBase.replace("."+entitatCodi+".", ".");
		}
		return keyBase;
    }

    @Override
    protected void afterConversion(ConfigResourceEntity entity, ConfigResource resource) {
        if ("CREDENTIALS".equals(resource.getType().getId()) && resource.getValue() != null) {
            resource.setValue( "********" );
        }
    }

    private class AlternativeValuePerspectiveApplicator implements PerspectiveApplicator<ConfigResourceEntity, ConfigResource> {

        @Override
        public void applySingle(String code, ConfigResourceEntity entity, ConfigResource resource) {
            if (resource.getValue() == null) {
                if (resource.getEntitatCodi() != null) {
                    resource.setAlternativeValue( configHelper.getConfigForEntitat(
                            resource.getEntitatCodi(), getKeyBase(resource.getKey(), resource.getEntitatCodi()) ) );
                } else {
                    resource.setAlternativeValue( configHelper.getConfig( resource.getKey() ) );
                }

                if ("CREDENTIALS".equals(resource.getType().getId()) && resource.getAlternativeValue() != null) {
                    resource.setAlternativeValue( "********" );
                }
            }
        }

    }

    private class SincronitzarActionExecutor implements ActionExecutor<ConfigResourceEntity, Serializable, Serializable> {

        @Override
        public Serializable exec(String code, ConfigResourceEntity entity, Serializable params) throws ActionExecutionException {
            configHelper.synchronize();
            return null;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }

    private class RestartTaskActionExecutor implements ActionExecutor<ConfigResourceEntity, Serializable, Serializable> {

        @Override
        public Serializable exec(String code, ConfigResourceEntity entity, Serializable params) throws ActionExecutionException {
            schedulingConfig.restartSchedulledTasks("totes");
            return null;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }

}