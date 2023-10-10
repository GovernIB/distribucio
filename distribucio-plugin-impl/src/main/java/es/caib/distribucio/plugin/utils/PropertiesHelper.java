/**
 * 
 */
package es.caib.distribucio.plugin.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per accedir a les entrades del fitxer de properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.distribucio.properties.path";
	private static PropertiesHelper instance = null;
	private boolean llegirSystem = true;

	public static PropertiesHelper getProperties() {
		if (instance == null) {
			instance = new PropertiesHelper();
			String propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
			if (propertiesPath != null) {
				instance.llegirSystem = false;
				logger.debug("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				try {
					if (propertiesPath.startsWith("classpath:")) {
						instance.load(
								PropertiesHelper.class.getClassLoader().getResourceAsStream(
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
			return System.getProperty(key);
		else
			return super.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		return Boolean.valueOf(getProperty(key)).booleanValue();
	}
	public int getAsInt(String key) {
		return Integer.valueOf(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return Long.valueOf(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return Float.valueOf(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return Double.valueOf(getProperty(key)).doubleValue();
	}

	public boolean isLlegirSystem() {
		return llegirSystem;
	}
	public void setLlegirSystem(boolean llegirSystem) {
		this.llegirSystem = llegirSystem;
	}

	public Properties findAll() {
		return findByPrefix(null);
	}
	public Properties findByPrefix(String prefix) {
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
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
