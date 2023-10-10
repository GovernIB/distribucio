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


}
