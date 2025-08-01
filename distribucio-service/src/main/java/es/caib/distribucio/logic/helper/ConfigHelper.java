package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.persist.entity.ConfigEntity;
import es.caib.distribucio.persist.entity.ConfigGroupEntity;
import es.caib.distribucio.persist.repository.ConfigGroupRepository;
import es.caib.distribucio.persist.repository.ConfigRepository;

@Component
public class ConfigHelper {

	@Autowired
	private ConfigRepository configRepository;
	@Autowired
	private ConfigGroupRepository configGroupRepository;
	@Autowired
	private LoadedPropertiesHelper loadedPropertiesHelper;
	@Autowired
	private Environment springEnvironment;

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
		for (ConfigEntity configEntity: configs) {
			String prop = getEnvironmentProperty(configEntity.getKey(), null);
			if (prop != null) {
				configEntity.updateValue(prop);
			}
		}
		List<ConfigGroupEntity> configGroups = configGroupRepository.findAll();
		for (ConfigGroupEntity configGroupEntity: configGroups) {
			loadedPropertiesHelper.reloadProperties(configGroupEntity.getKey());
		}
	}

	@Transactional(readOnly = true)
	public String getConfig(EntitatDto entitatActual, String key) {
		return getConfigForEntitat(entitatActual != null ? entitatActual.getCodi() : null, key);
	}

	@Transactional(readOnly = true)
	public String getConfigForEntitat(String entitatCodi, String key) {
		String value = null;
		if (key != null) {
			Optional<ConfigEntity> configEntity = configRepository.findById(key);
			if (configEntity.isPresent()) {
				// Propietat trobada
				if (configEntity.get().isConfigurable() && entitatCodi != null) {
					// Propietat a nivell d'entitat
					String keyEntitat = convertirKeyGeneralToKeyPropietat(entitatCodi, key);
					Optional<ConfigEntity> configEntitatEntity = configRepository.findById(keyEntitat);
					if (configEntitatEntity.isPresent()) {
						value = getConfig(configEntitatEntity.get());
					}
				}
				if (value == null) {
					// Propietat global
					value = getConfig(configEntity.get());
				}
			} else {
				// Propietat JBoss
				value = getEnvironmentProperty(key, null);
			}
		} else {
			// Propietat JBoss
			value = getEnvironmentProperty(key, null);
		}
		return value;
	}

	@Transactional(readOnly = true)
	public String getConfig(String key) {
		EntitatDto entitatActual = ConfigHelper.entitat.get();
		return this.getConfig(entitatActual, key);
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
		configRepository.saveAll(confs);
	}

	public void deleteConfigEntitat(String codiEntitat) {
		configRepository.deleteByEntitatCodi(codiEntitat);
	}

	@Transactional(readOnly = true)
	public Map<String, String> getGroupProperties(String codeGroup) {
		Map<String, String> properties = new HashMap<>();
		Optional<ConfigGroupEntity> configGroup = configGroupRepository.findById(codeGroup);
		fillGroupProperties(configGroup.orElse(null), properties);
		return properties;
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
		return getConfig(key) != null ? Integer.valueOf(getConfig(key)) : null;
	}
	public Long getAsLong(String key) {
		return getConfig(key) != null ? Long.valueOf(getConfig(key)) : null;
	}
	public Float getAsFloat(String key) {
		return getConfig(key) != null ? Float.valueOf(getConfig(key)) : null;
	}

	/** Obté totes les propietats Jboss i per entitat. Els valors per entitat
	 * prevalen sobre els de jboss.
	 */
	@Transactional(readOnly = true)
	public Properties getAllProperties(String entitatCodi) {
		Properties properties = getAllEnvironmentProperties();
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

	public String getEnvironmentProperty(String key, String defaultValue) {
		String value = springEnvironment.getProperty(key);
		return (value != null) ? value : defaultValue;
	}

	public Properties getAllEnvironmentProperties() {
		Properties properties = new Properties();
		MutablePropertySources propertySources = ((AbstractEnvironment)springEnvironment).getPropertySources();
		StreamSupport.stream(propertySources.spliterator(), false).
				filter(ps -> ps instanceof EnumerablePropertySource).
				map(ps -> ((EnumerablePropertySource<?>)ps).getPropertyNames()).
				flatMap(Arrays::<String>stream).
				forEach(propName -> properties.setProperty(propName, springEnvironment.getProperty(propName)));
		return properties;
	}

	private String getConfig(ConfigEntity configEntity) {
		if (configEntity.isJbossProperty()) {
			// Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
			return getEnvironmentProperty(configEntity.getKey(), configEntity.getValue());
		}
		return configEntity.getValue();
	}

	private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    @Transactional(readOnly = true)
    public boolean hasEntityGroupPropertiesModified(String entitatCodi, String grupCodi) {
    
        if (entitatCodi == null) {
            return false;
        }
    
        var configuracions = configRepository.findByGrupCodi(grupCodi);

        if (configuracions == null || configuracions.isEmpty()) {
            return false;
        }

        Map<String, String> globalPproperties = new HashMap<>();
        Map<String, String> entityProperties = new HashMap<>();

        // Obtenir les propietats a nivell global
        configuracions
                .stream().filter(config -> config.getEntitatCodi() == null)
                .forEach(config -> globalPproperties.put(getKeyName(config.getKey(), null), springEnvironment.getProperty(config.getKey())));
    
        // Obtenir les propietats a nivell d'entitat
        configuracions
                .stream().filter(config -> entitatCodi.equals(config.getEntitatCodi()))
                .forEach(config -> entityProperties.put(getKeyName(config.getKey(), entitatCodi), springEnvironment.getProperty(config.getKey())));
    
        // Compara les propietats i retorna el resultat
        for (var entry : entityProperties.entrySet()) {
            String globalValue = globalPproperties.get(entry.getKey());
            String entityValue = entry.getValue();
    
            if (entityValue != null && !entityValue.equals(globalValue)) {
                return true;
            }
        }
    
        return false;
    }
    
    private String getKeyName(String key, String entitatCodi) {
        if (entitatCodi == null) {
            return key.substring(ConfigDto.prefix.length() + 1);
        } else {
            return key.substring(ConfigDto.prefix.length() + entitatCodi.length() + 2);
        }
    }

	/*@SuppressWarnings("serial")
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
							properties.put(keystr, System.getProperty(keystr));
						}
					}
				}
			} else {
				for (Object key: this.keySet()) {
					if (key instanceof String) {
						String keystr = (String)key;
						if (keystr.startsWith(prefix)) {
							properties.put(keystr, getProperty(keystr));
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
							properties.put(keystr, System.getProperty(keystr));
						}
					}
				}
			} else {
				for (Object key: this.keySet()) {
					if (key instanceof String) {
						String keystr = (String)key;
						if (prefix == null || keystr.startsWith(prefix)) {
							properties.put(keystr, getProperty(keystr));
						}
					}
				}
			}
			return properties;
		}
		private static final Logger logger = LoggerFactory.getLogger(JBossPropertiesHelper.class);
	}*/

}
