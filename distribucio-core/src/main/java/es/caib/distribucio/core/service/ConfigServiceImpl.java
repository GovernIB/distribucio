package es.caib.distribucio.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.ConfigGroupDto;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.entity.ConfigEntity;
import es.caib.distribucio.core.entity.ConfigGroupEntity;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.repository.ConfigGroupRepository;
import es.caib.distribucio.core.repository.ConfigRepository;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        ConfigEntity configEntity = configRepository.findOne(property.getKey());
        configEntity.updateValue(property.getValue());
        pluginHelper.reloadProperties(configEntity.getGroupCode());
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {
        List<ConfigGroupDto> configGroupDtoList =  conversioTipusHelper.convertirList(
                configGroupRepository.findByParentCodeIsNull(new Sort(Sort.Direction.ASC, "position")),
                ConfigGroupDto.class);

        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }
    
    @Override
    @Transactional
    public void synchronize() {
    	List<ConfigEntity> configs = configRepository.findByJbossPropertyFalse();
    	
    	for (ConfigEntity configEntity : configs) {
    		String prop = ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(configEntity.getKey());
			if (prop != null) {
    			configEntity.updateValue(prop);
			}
    		
		}
    	
    	List<ConfigGroupEntity> configGroups = configGroupRepository.findAll();
    	for (ConfigGroupEntity configGroupEntity : configGroups) {
    		pluginHelper.reloadProperties(configGroupEntity.getKey());
		}
    	
    }

    private void processPropertyValues(ConfigGroupDto cGroup) {
        for (ConfigDto config: cGroup.getConfigs()) {
            if (config.isJbossProperty()) {
                config.setValue(ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(config.getKey()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }
}
