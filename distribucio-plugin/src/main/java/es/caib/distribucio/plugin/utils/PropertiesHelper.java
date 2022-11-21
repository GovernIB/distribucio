/**
 * 
 */
package es.caib.distribucio.plugin.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

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
				//logger.debug("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				InputStream is = null;
				try {
					if (propertiesPath.startsWith("classpath:")) {
						is = 
								PropertiesHelper.class.getClassLoader().getResourceAsStream(
										propertiesPath.substring("classpath:".length()));
					} else if (propertiesPath.startsWith("file://")) {
						is = new FileInputStream(
								propertiesPath.substring("file://".length()));
					} else {
						is = new FileInputStream(propertiesPath);
					}
					instance.load(is);
				} catch (Exception ex) {
					System.err.println("No s'han pogut llegir els properties: " + ex.getMessage());
					ex.printStackTrace();
				} finally {
					try {is.close(); }catch(Exception e) {}
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
		return new Boolean(getProperty(key)).booleanValue();
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key)).doubleValue();
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
	
//	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
