package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.dto.ConfigGroupDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.core.config.SegonPlaConfig;
import es.caib.distribucio.core.entity.ConfigEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.repository.ConfigGroupRepository;
import es.caib.distribucio.core.repository.ConfigRepository;
import es.caib.distribucio.core.repository.EntitatRepository;

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
    private EntitatRepository entitatRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private SegonPlaConfig segonPlaConfig;
    
    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        ConfigEntity configEntity = configRepository.findOne(property.getKey());
        configEntity.updateValue(!"null".equals(property.getValue()) ? property.getValue() : null);
        pluginHelper.reloadProperties(configEntity.getGroupCode());
        pluginHelper.resetPlugins();
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
    }

    
    /** Mètode que revisa després d'iniciar Distribucio que totes les entitats tinguin una entrada
     * per cada propietat configurable a nivell d'entitat.
     */
    @PostConstruct
    @Transactional
    public void postConstruct() {
		// Recuperar totes les propietats configurables que no siguin d'entitat
    	List<ConfigEntity> listConfigEntity = configRepository.findConfigurablesAmbEntitatNull();
    	List<ConfigEntity> llistatPropietatsConfigurables = configRepository.findConfigurables();
	    List<EntitatEntity> llistatEntitats = entitatRepository.findAll();
	    int propietatsNecessaries = listConfigEntity.size() * (llistatEntitats.size() + 1);
		// Mirar que la propietat existeixi per a la entitat, si no crear-la amb el valor null
	    if (llistatPropietatsConfigurables.size() != propietatsNecessaries) {
		    for (ConfigEntity cGroup : listConfigEntity) {
		    	int lengthKey = cGroup.getKey().length();
		    	for (EntitatEntity entitat : llistatEntitats) {
		    		if (cGroup.getEntitatCodi() == null) {
		        		String cercarPropietat = cGroup.getKey().substring(0, 20) + entitat.getCodi() + cGroup.getKey().substring(19, lengthKey);
		        		ConfigEntity configEntity = configRepository.findPerKey(cercarPropietat);
		        		if (configEntity == null) {
		        			ConfigEntity novaPropietat = new ConfigEntity();
			        		novaPropietat.setDescription(cGroup.getDescription());
			        		novaPropietat.setEntitatCodi(entitat.getCodi());
			        		novaPropietat.setGroupCode(cGroup.getGroupCode());
			        		novaPropietat.setJbossProperty(cGroup.isJbossProperty());
			        		novaPropietat.setKey(cercarPropietat);
			        		novaPropietat.setPosition(cGroup.getPosition());
			        		novaPropietat.setConfigurable(cGroup.isConfigurable());			        		
			        		novaPropietat.setTypeCode(cGroup.getTypeCode());
			        		
		                    logger.info("Guardant la propietat: " + novaPropietat.getKey());		        		
			        		configRepository.save(novaPropietat);
		        		}
		    		}
		    	}
		    }	
	    }

    }
    
    @Override
    @Transactional
	public List<ConfigDto> findAllPerEntitat(EntitatDto entitat) {
    	List<ConfigEntity> llistatConfiguracionsEntitat = configRepository.findAllPerEntitat(entitat.getCodi());
        List<ConfigDto> llistatPropietatsEntitat = new ArrayList<ConfigDto>();
    	for (ConfigEntity cEntity : llistatConfiguracionsEntitat) {
        	ConfigDto configDto = new ConfigDto();
        	configDto.setDescription(cEntity.getDescription());
        	configDto.setEntitatCodi(cEntity.getEntitatCodi());
        	configDto.setJbossProperty(cEntity.isJbossProperty());
        	configDto.setKey(cEntity.getKey());
        	configDto.setTypeCode(cEntity.getTypeCode());
        	configDto.setGroupCode(cEntity.getGroupCode());
        	configDto.setTypeCode(cEntity.getTypeCode());        	
        	configDto.setValidValues(cEntity.getValidValues());
        	String valorPropietat = "";
    		String propietatGenerica = cEntity.getKey().replace(cEntity.getEntitatCodi() + ".", "");
    		ConfigEntity configGeneric = configRepository.findPerKey(propietatGenerica);
        	if (cEntity.getValue() == null) {
        		valorPropietat = configGeneric.getValue();
        		if (valorPropietat == null) {
        			configDto.setValue(valorPropietat);
        		} else {
        			configDto.setValue(valorPropietat + " ////");
        		}
        	}else {
            	configDto.setValue(cEntity.getValue());
        	}
        	
        	llistatPropietatsEntitat.add(configDto);
        }
    	return llistatPropietatsEntitat;
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
    	configHelper.synchronize();
    }
    
    
    @Override
    @Transactional
	public void reiniciarTasquesEnSegonPla() {
		segonPlaConfig.reiniciarTasquesSegonPla();
	}
    

	@Override
	@Transactional
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		
		String[] splitKey = key.split(ConfigDto.getPrefix());
		if (splitKey[1] == null) {
			logger.error("Entitat config key no trobada. Key: " + key);
			return new ArrayList<>();
		}
		return conversioTipusHelper.convertirList(configRepository.findLikeKeyEntitatNotNullAndConfigurable(splitKey[1]), ConfigDto.class);
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

	@Override
    @Transactional(readOnly=true)
	public String getConcsvBaseUrl() {
		return configHelper.getConfig("es.caib.distribucio.concsv.base.url");
	}

	@Override
    @Transactional(readOnly=true)
	public ConfigDto findByKey(String key) {
        ConfigEntity configEntity = configRepository.findOne(key);
        return conversioTipusHelper.convertir(configEntity, ConfigDto.class);
	}


	@Override
	public void setEntitatPerPropietat(EntitatDto entitatDto) {
		ConfigHelper.setEntitat(entitatDto);
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);


	@Override
	public String getTempsErrorsMonitorIntegracio() {
		return configHelper.getConfig("es.caib.distribucio.monitor.integracio.errors.temps");
	}

}
