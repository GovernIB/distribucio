package es.caib.distribucio.core.helper;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.entity.ConfigEntity;
import es.caib.distribucio.core.entity.ConfigGroupEntity;
import es.caib.distribucio.core.repository.ConfigGroupRepository;
import es.caib.distribucio.core.repository.ConfigRepository;

@Component
public class ConfigHelper {

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private PluginHelper pluginHelper;
    
    
    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();    
    
    public static ThreadLocal<EntitatDto> getEntitat() {
		return entitat;
	}


	public static void setEntitat(EntitatDto entitat) {
		ConfigHelper.entitat.set(entitat);
	}


	@PostConstruct
    public void firstSincronization() {
		if (configRepository.countNotNullValues() == 0) {
			synchronize();
		}
    }
    
    
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
    
    @Transactional(readOnly = true)
    public String getConfig(EntitatDto entitatActual, String key) {

		key = convertirKeyGeneralToKeyPropietat(entitatActual, key);
		//logger.debug("Entitat actual per les propietats : " + (entitatActual != null ? entitatActual.getCodi() : ""));

		ConfigEntity configEntity = new ConfigEntity();
        configEntity = configRepository.findOne(key);
        if (configEntity != null && configEntity.getValue() == null 
    		&& !configEntity.getGroupCode().equals("USUARIS")) {
        	String replace = "." + entitatActual.getCodi();
        	key = key.replace(replace, "");
        	configEntity = configRepository.findOne(key);
        }
        if (configEntity == null && entitatActual != null || 
        	configEntity != null && configEntity.getValue() == null && 
        	!configEntity.getGroupCode().equals("USUARIS") && !configEntity.isJbossProperty()) {
        	String replace = "." + entitatActual.getCodi();
        	key = key.replace(replace, "");
        	configEntity = configRepository.findOne(key);
        } 
        
		if (configEntity != null) {
			return getConfig(configEntity);
		} else {
			return getJBossProperty(key);
		}
    }
    
    @Transactional(readOnly = true)
    public String getConfig(String key)  {    	
    	
		EntitatDto entitatActual = ConfigHelper.entitat.get();
		
		//key = convertirKeyGeneralToKeyPropietat(entitatActual, key);
		
		return this.getConfig(entitatActual, key);
		
	}
	
	private String convertirKeyGeneralToKeyPropietat (EntitatDto entitatActual, String key) {
		if (entitatActual != null && !key.contains(entitatActual.getCodi())) {
			String keyReplace = key.replace(".", "_");
			String[] splitKey = keyReplace.split("_");
			String keyEntitat = "";
			for (int i=0; i<splitKey.length; i++) {
				if (i == (splitKey.length - 1)) {
					keyEntitat = keyEntitat + splitKey[i];
				}else if (i == 2){
					keyEntitat = keyEntitat + splitKey[i] + "." + entitatActual.getCodi() + ".";
				}else {				
					keyEntitat = keyEntitat + splitKey[i] + ".";
				}
			}
			key = keyEntitat;
		}
		return key;
	}
    
	@Transactional(readOnly = true)
	public String getConfig(String key, String defaultValue) {
		String value = getConfig(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public void crearConfigsEntitat(String codiEntitat) {
		
		List<ConfigEntity> configs = configRepository.findConfigurablesAmbEntitatNull();
		ConfigDto dto = new ConfigDto();
		dto.setEntitatCodi(codiEntitat);
		ConfigEntity nova;
		List<ConfigEntity> confs = new ArrayList<>();
		for (ConfigEntity config : configs) {
			dto.setKey(config.getKey());
			String key = dto.crearEntitatKey();
			nova = new ConfigEntity();
			nova.crearConfigNova(key, codiEntitat, config);
			confs.add(nova);
		}
		configRepository.save(confs);
	}
	
	public void deleteConfigEntitat(String codiEntitat) {
		configRepository.deleteByEntitatCodi(codiEntitat);
	}

    @Transactional(readOnly = true)
    public Map<String, String> getGroupProperties(String codeGroup) {
        Map<String, String> properties = new HashMap<>();
        ConfigGroupEntity configGroup = configGroupRepository.findOne(codeGroup);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroupEntity configGroup, Map<String, String> outProperties) {
        if (configGroup == null) {
            return;
        }
        for (ConfigEntity config : configGroup.getConfigs()) {
        	
			String conf = getConfig(config);
			if (conf != null) {
				outProperties.put(config.getKey(), conf);
			} else {
				logger.debug("Propietat: " + config.getKey() + " es null");
			}
        	
            outProperties.put(config.getKey(), getConfig(config));
        }

        for (ConfigGroupEntity child : configGroup.getInnerConfigs()) {
            fillGroupProperties(child, outProperties);
        }
    }
    
    public Boolean getAsBoolean(String key, boolean defaultValue) {
		return getConfig(key) != null ? Boolean.parseBoolean(getConfig(key)) : defaultValue;
    }

    public Boolean getAsBoolean(String key) {
		return getConfig(key) != null ? Boolean.parseBoolean(getConfig(key)) : null;
    }
    public Integer getAsInt(String key) {
    	return getConfig(key) != null ? new Integer(getConfig(key)) : null;
    }
    public Long getAsLong(String key) {
        return getConfig(key) != null ? new Long(getConfig(key)) : null;
    }
    public Float getAsFloat(String key) {
        return getConfig(key) != null ? new Float(getConfig(key)) : null;
    }

    public String getJBossProperty(String key) {
        return JBossPropertiesHelper.getProperties().getProperty(key);
    }
    public String getJBossProperty(String key, String defaultValue) {
        return JBossPropertiesHelper.getProperties().getProperty(key, defaultValue);
    }

    private String getConfig(ConfigEntity configEntity) {
        if (configEntity.isJbossProperty()) {
            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
            return getJBossProperty(configEntity.getKey(), configEntity.getValue());
        }
        return configEntity.getValue();
    }
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    public static class JBossPropertiesHelper extends Properties {

        private static final String APPSERV_PROPS_PATH = "es.caib.distribucio.properties.path"; //in jboss is null

        private static JBossPropertiesHelper instance = null;

        private boolean llegirSystem = true;

        public static JBossPropertiesHelper getProperties() {
            return getProperties(null);
        }
        public static JBossPropertiesHelper getProperties(String path) {
            String propertiesPath = path;
            if (propertiesPath == null) {
                propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
            }
            if (instance == null) {
                instance = new JBossPropertiesHelper();
                if (propertiesPath != null) {
                    instance.llegirSystem = false; //a jboss no entrem aquí
                    logger.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
                    try {
                        if (propertiesPath.startsWith("classpath:")) {
                            instance.load(
                                    JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(
                                            propertiesPath.substring("classpath:".length())));
                        } else if (propertiesPath.startsWith("file://")) {
                            FileInputStream fis = new FileInputStream(
                                    propertiesPath.substring("file://".length()));
                            instance.load(fis);
                        } else {
                            FileInputStream fis = new FileInputStream(propertiesPath);
                            instance.load(fis);
                        }
                    } catch (Exception ex) {
                        logger.error("No s'han pogut llegir els properties", ex);
                    }
                }
            }
            return instance;
        }

        public String getProperty(String key) {
            if (llegirSystem)
                return System.getProperty(key); //jboss
            else
                return super.getProperty(key); //jboss
        }
        public String getProperty(String key, String defaultValue) {
            String val = getProperty(key);
            return (val == null) ? defaultValue : val;
        }

        public boolean isLlegirSystem() {
            return llegirSystem;
        }
        public void setLlegirSystem(boolean llegirSystem) {
            this.llegirSystem = llegirSystem;
        }


        public Properties findAll() {
            return findByPrefixProperties(null);
        }

        public Map<String, String> findByPrefix(String prefix) {
            Map<String, String> properties = new HashMap<String, String>();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }

        public Properties findByPrefixProperties(String prefix) {
            Properties properties = new Properties();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
        
    	private static final Logger logger = LoggerFactory.getLogger(JBossPropertiesHelper.class);
    }
    
}
