package es.caib.distribucio.plugin;

import java.util.Properties;


public abstract class DistribucioAbstractPluginProperties {
	
	private Properties properties = null;

	public DistribucioAbstractPluginProperties() {
		this((Properties) null);
	}
	public DistribucioAbstractPluginProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return this.properties;
	}
	
	public String getProperty(String key) {
		return this.getProperties().getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		return this.getProperties().getProperty(key, defaultValue);
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


}
