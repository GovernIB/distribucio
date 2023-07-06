package es.caib.distribucio.core.helper;


import java.io.FileInputStream;
import java.io.InputStream;
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
    
    /** Per guardar l'entitat actual per a les propietats multi entitat. */
    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();    
    
    public static EntitatDto getEntitat() {
		return entitat.get();
	}
	public static void setEntitat(EntitatDto entitat) {
		ConfigHelper.entitat.set(entitat);
	}
	public static String getEntitatActualCodi() {
		EntitatDto entitat = getEntitat();
		return entitat != null ? entitat.getCodi() : null;
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
    	return getConfigForEntitat(entitatActual != null ? entitatActual.getCodi() : null, key);
    }
    
    @Transactional(readOnly = true)
    public String getConfigForEntitat(String entitatCodi, String key) {
		String value = null;
		if(key!=null) {
			ConfigEntity configEntity = configRepository.findOne(key);
			if (configEntity != null) {
				// Propietat trobada
				if (configEntity.isConfigurable() && entitatCodi != null) {
		    		// Propietat a nivell d'entitat
		    		String keyEntitat = convertirKeyGeneralToKeyPropietat(entitatCodi, key);
		    		ConfigEntity configEntitatEntity = configRepository.findOne(keyEntitat);
		            if (configEntitatEntity != null) {
		            	value = getConfig(configEntitatEntity);
		            }
				}
				if (value == null) {
					// Propietat global
					value = getConfig(configEntity);
				}
			} else {
				// Propietat JBoss
				value = getJBossProperty(key);
			}
    }	else {
    		
    	// Propietat JBoss
		value = getJBossProperty(key);
    }
    	
    
		return value;
    }
    
    @Transactional(readOnly = true)
    public String getConfig(String key)  {    	
    	
		EntitatDto entitatActual = ConfigHelper.entitat.get();
				
		return this.getConfig(entitatActual, key);
	}
	
	private String convertirKeyGeneralToKeyPropietat (String entitatActualCodi, String key) {
		if (entitatActualCodi != null && !key.contains(entitatActualCodi)) {
			String keyReplace = key.replace(".", "_");
			String[] splitKey = keyReplace.split("_");
			String keyEntitat = "";
			for (int i=0; i<splitKey.length; i++) {
				if (i == (splitKey.length - 1)) {
					keyEntitat = keyEntitat + splitKey[i];
				}else if (i == 2){
					keyEntitat = keyEntitat + splitKey[i] + "." + entitatActualCodi + ".";
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
    

    /** Obté totes les propietats Jboss i per entitat. Els valors per entitat
     * prevalen sobre els de jboss.
     */
    @Transactional(readOnly = true)
	public Properties getAllProperties(String entitatCodi) {
		Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity config: configs) {
             String value = getConfigForEntitat(entitatCodi, config.getKey());
            if (value != null) {
                properties.put(config.getKey(), value);
            } else if ( !config.isJbossProperty()) {
            	properties.remove(config.getKey());
            }
        }
        return properties;
	}

    /** Obté totes les propietats per a un codi d'entitat s'usa per inicalitzar els plugins.
     */
    @Transactional(readOnly = true)
	public Properties getAllEntityProperties(String entitatCodi) {
        Properties properties = new Properties();
        
        List<ConfigEntity> configs = configRepository.findByEntitatCodiIsNull();
        for (ConfigEntity config: configs) {
             String value = getConfigForEntitat(entitatCodi, config.getKey());
            if (value != null) {
                properties.put(config.getKey(), value);
            }
        }
        return properties;
	}
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    @SuppressWarnings("serial")
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
                    InputStream is = null;
                    try {
                        if (propertiesPath.startsWith("classpath:")) {
                        	is = JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(
                                    propertiesPath.substring("classpath:".length()));
                        } else if (propertiesPath.startsWith("file://")) {
                            is = new FileInputStream(
                                    propertiesPath.substring("file://".length()));
                        } else {
                            is = new FileInputStream(propertiesPath);
                        }
                        instance.load(is);
                    } catch (Exception ex) {
                        logger.error("No s'han pogut llegir els properties", ex);
                    } finally {
                    	if (is != null) {
                    		try {
                    			is.close();
                    		} catch(Exception e) {
                    			logger.error("Error tancant l'input stream " + is.toString() + ": " + e.getMessage(), e);;
                    		}
                    	} 
                    }
                }
            }
            return instance;
        }

        public String getProperty(String key) {
            if (llegirSystem && key!=null)
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
